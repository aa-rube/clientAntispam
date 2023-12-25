package app.bot.controller;

import app.bot.config.BotConfig;
import app.bot.enviroment.messages.*;
import app.handle.StopWordHandle;
import app.handle.AdvHandler;
import app.handle.VipUserHandle;
import app.img.DownloadImg;
import app.img.IAmToken;
import app.model.*;
import app.service.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class Chat extends TelegramLongPollingBot {
    @Autowired
    private BotConfig botConfig;
    @Autowired
    private StartMenuMessage startMessage;
    @Autowired
    private StopWordMessage stopWordMessage;
    @Autowired
    private StopWordHandle stopWordHandler;
    @Autowired
    private AdvMessage advMessage;
    @Autowired
    private AdvHandler advHandler;
    @Autowired
    private StopWordService stopWordService;
    @Autowired
    private AdvService advService;
    @Autowired
    private AdminMessage adminMessage;
    @Autowired
    private DownloadImg downloadImg;
    @Autowired
    private IAmToken iAmToken;
    @Autowired
    private PartnersMessage partnersMsg;
    @Autowired
    private VipService vipService;
    @Autowired
    private UserMessage userMessage;
    @Autowired
    UserClientService userClientService;
    private String yandexMainToken;
    @Autowired
    private GroupListenService groupListenService;
    @Autowired
    private OwnersGroupsMessage ownersGroupsMessage;
    @Autowired
    private VipUserHandle vipUserHandle;
    @Autowired
    private GodMessage godMessage;
    private final HashMap<Long, UserClient> clients = new HashMap<>();
    private Map<Long, List<String>> stopUserWords = new HashMap<>();
    private final HashMap<String, Long> clientsGroups = new HashMap<>();
    private final HashMap<Long, Integer> sendApproveMsg = new HashMap<>();
    private final HashSet<Long> waitForNewOwnerGroup = new HashSet<>();
    private final HashSet<Long> waitForNewVipUser = new HashSet<>();
    private final HashSet<Long> waitForScreenShot = new HashSet<>();
    private final HashMap<Long, AdvUser> createNewAdvUser = new HashMap<>();
    private final HashSet<Long> enterNewStopWord = new HashSet<>();
    private final HashMap<Long, Integer> chatIdMsgId = new HashMap<>();
    private final HashSet<String> advertisersUsersNames = new HashSet<>();
    private final HashMap<Long, List<Integer>> messageToDelete = new HashMap<>();
    private final HashSet<String> vipUsers = new HashSet<>();
    private final HashMap<String, String> groupMediaData = new HashMap<>();
    private final HashMap<Long, LocalDateTime> pretendedMute = new HashMap<>();
    private final HashMap<Long, Long> wereMuted = new HashMap<>();
    private final HashSet<Long> groupsChatId = new HashSet<>();
    private final int aLongTime = (int) (System.currentTimeMillis() / 1000) + 10 * 365 * 24 * 60 * 60;
    private final StringBuilder builder = new StringBuilder();

    @Scheduled(fixedRate = 10800000 * 2)
    private void checkEndTime() {
        LocalDateTime now = LocalDateTime.now();

        for (UserClient client : userClientService.findAll()) {
            if (client.isPaid() && client.getSubscriptionEnd().isBefore(now)) {
                client.setPaid(false);
                userClientService.save(client);
                executeMsg(userMessage.payIsOff(builder, client));
            }
        }
    }
    @Scheduled(fixedRate = 10800000)
    public void getIamToken() {
        yandexMainToken = iAmToken.getIAmToken(botConfig.getYandexToken(), yandexMainToken);
        groupMediaData.clear();
    }
    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }
    public Long getOwnerChatId() {
        return botConfig.getOwnerChatId();
    }
    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
    @PostConstruct
    public void init() {
        stopUserWords.clear();
        advertisersUsersNames.clear();
        wereMuted.put(1L, 2L);

        for (UserClient client : userClientService.findAll()) {
            clients.put(client.getChatId(), client);
        }

        stopUserWords = stopWordService.getAllStopWords().stream()
                .collect(Collectors.groupingBy(StopWordObject::getOwnerChatId,
                        Collectors.mapping(StopWordObject::getWord, Collectors.toList())));

        for (GroupToListen group : groupListenService.findAll()) {
            clientsGroups.put(group.getGroupUserName(), group.getOwnerChatId());
        }


        for (VipUser user : vipService.findAllVipUsers()) {
            vipUsers.add(user.getUserName());
        }

        advertisersUsersNames.addAll(advService.getAllUsers().stream()
                .map(AdvUser::getUserName)
                .toList());
    }
    @Override
    public void onUpdateReceived(Update update) {

        try {
            if (update.hasCallbackQuery()) {
                Long chatId = update.getCallbackQuery().getFrom().getId();
                if(!clients.get(chatId).isPaid() && !chatId.equals(getOwnerChatId())) return;

                userClientHandler(update);
                callBackDataHandle(update);
                return;
            }

            if (update.hasMessage() && (update.getMessage().hasPhoto() || update.getMessage().hasDocument())) {
                Long chatId = update.getMessage().getChatId();

                if (waitForScreenShot.contains(chatId)) {
                    sendPayProveToAdmin(chatId, update.getMessage());
                    waitForScreenShot.remove(chatId);
                    return;
                }
            }

            if (update.hasMessage() && update.getMessage().hasText()) {
                if ((clients.containsKey(update.getMessage().getChatId())

                        && clients.get(update.getMessage().getChatId()).isPaid())
                        || update.getMessage().getText().equals("/start")
                        || update.getMessage().getChatId().equals(getOwnerChatId())) {

                    textMessageHandle(update);
                    return;
                }
            }

        } catch (Exception ignored) {}

        if (update.hasMessage()) {
            Message message = update.getMessage();
            String group = "@" + update.getMessage().getChat().getUserName();

            if (clientsGroups.containsKey(group)) {
                Long ownerChatId = clientsGroups.get(group);

                if (clients.get(ownerChatId).isPaid()) {

                    String userName = "@" + message.getFrom().getUserName();
                    if (vipUsers.contains(userName)) {
                        return;
                    }

                    if (advertisersUsersNames.contains(userName)) {
                        controlPartner(ownerChatId, message, userName);

                    } else {
                        stopSpam(ownerChatId, message);
                    }
                }
            }
        }
    }
    private void sendPayProveToAdmin(Long chatId, Message message) {
        int msgId = 0;
        try {
            if (message.hasDocument()) {
               msgId = execute(godMessage.screenDoc(builder,chatId, getOwnerChatId(), message)).getMessageId();
            }
           msgId = execute(godMessage.screenPhoto(builder,chatId, getOwnerChatId(), message)).getMessageId();
            execute(userMessage.waitForCheckThePay(builder, chatId));
        } catch (Exception e) {}
        sendApproveMsg.put(chatId, msgId);
    }
    public void controlPartner(Long ownerChatId, Message message, String userName) {

        advService.getAllUsersByUserName(userName).stream()
                .filter(user -> user.getPermissionToGroup()
                        .equals("@" + message.getChat().getUserName())).findFirst()
                .ifPresent(user -> {

                    if (!user.isStarted()) {
                        user.setStarted(true);
                        user.setEndPermission(LocalDateTime.now().plusDays(user.getDaysOfPermission()));
                    }

                    if (user.getEndPermission().isBefore(LocalDateTime.now())) {
                        stopSpam(ownerChatId, message);

                        executeWithoutDelete(message.getChatId(),
                                partnersMsg.userTimeEndMsgToUser(builder,user));
                        executeWithoutDelete(user.getAdminChatIdOwner(),
                                partnersMsg.userTimeEndMsgToAdmin(builder,user));
                        return;
                    }

                    int count = user.getPostCount();
                    if (count <= 0) {
                        stopSpam(ownerChatId, message);

                        executeWithoutDelete(message.getFrom().getId(),
                                partnersMsg.postCountEndMsgToUser(builder,user));
                        executeWithoutDelete(user.getAdminChatIdOwner(),
                                partnersMsg.postCountEndMsgToAdmin(builder,user));
                        return;
                    }

                    if ((message.getMediaGroupId() != null && !groupMediaData.containsKey(message.getMediaGroupId()))
                            || message.getMediaGroupId() == null) {

                        try {
                            groupMediaData.put(message.getMediaGroupId(), userName);
                        } catch (Exception exception) {

                        }

                        count = count - 1;
                        user.setPostCount(count);
                        advService.autoDeleteByUserId(user.getId());
                        user.setId(null);
                        advService.save(user);
                        executeWithoutDelete(message.getFrom().getId(),
                                partnersMsg.postCounterMsgToUser(builder,user, count));
                        return;
                    }
                    stopSpam(ownerChatId, message);
                });
    }
    private void stopSpam(Long ownerChatId, Message message) {

        if (message.hasText() && !message.hasPhoto()) {
            deleteMessageIfTheTextIsBad(ownerChatId, message, message.getText().toLowerCase());
            return;
        }

        try {
            if (message.hasPhoto() && message.getCaption() != null) {
                deleteMessageIfTheTextIsBad(ownerChatId, message, message.getCaption().toLowerCase());
                deleteMessageIfThePhotoIsBad(ownerChatId, message);
                return;
            }

            if (message.hasPhoto() && message.getCaption() == null) {
                deleteMessageIfThePhotoIsBad(ownerChatId, message);
                return;
            }

            if (message.hasAnimation() || message.hasAudio() || message.hasDice() || message.hasDocument() ||
                    message.hasContact() || message.hasInvoice() || message.hasLocation() || message.hasSticker() ||
                    message.hasVideo() || message.hasVideoNote() || message.hasVoice() || message.hasAudio() ||
                    message.hasPoll() || message.hasPassportData() && message.getCaption() != null) {

                deleteMessageIfTheTextIsBad(ownerChatId, message, message.getCaption().toLowerCase());
            }
        } catch (Exception e) {
        }

    }
    private void deleteMessageIfTheTextIsBad(Long ownerChatId, Message message, String content) {
        stopUserWords.get(ownerChatId).stream()
                .filter(stopWord -> content.toLowerCase().contains(stopWord.toLowerCase().trim()))
                .forEach(stopWord -> {
                    mute(ownerChatId, message);
                    deleteBadMessage(message.getChatId(), message.getMessageId());
                    sendThisMessage(ownerChatId, message, stopWord);
                });
    }
    private void deleteMessageIfThePhotoIsBad(Long ownerChatId, Message message) throws Exception {
        if (!clients.get(ownerChatId).isCheckPhoto()) {
            return;
        }

        String filePhotoId = message.getPhoto().get(message.getPhoto().size() - 1).getFileId();
        GetFile getFile = new GetFile();
        getFile.setFileId(filePhotoId);
        File file = execute(getFile);

        deleteMessageIfTheTextIsBad(ownerChatId, message,

                downloadImg.getPhotoText(file, filePhotoId, botConfig.getToken(),

                        yandexMainToken, botConfig.getFolderYandexId()).toLowerCase());
    }
    private void mute(Long ownerChatId, Message message) {
        Long userId = message.getFrom().getId();
        Long groupId = message.getChatId();
        String userName = message.getFrom().getUserName();
        groupsChatId.add(groupId);

        if (pretendedMute.containsKey(userId)) {

            if (wereMuted.get(userId) != null && wereMuted.get(userId).equals(groupId)) {
                restrictUser(groupId, userId, aLongTime);
                wereMuted.remove(userId);

                executeWithoutDelete(ownerChatId,
                        "Пользователь " + userName + ", " + message.getFrom().getId() +
                                " замьючен на всегда в группе " + message.getChat().getUserName());
                return;
            }


            LocalDateTime lastOperationTime = pretendedMute.get(userId);
            LocalDateTime currentTime = LocalDateTime.now();
            long minutesElapsed = ChronoUnit.MINUTES.between(lastOperationTime, currentTime);

            if (minutesElapsed < 1) {
                int time = (int) (System.currentTimeMillis() / 1000) + 10800;
                wereMuted.put(userId, groupId);

                for (Long chatGroup : groupsChatId) {
                    Thread thread = new Thread(() -> {
                        restrictUser(chatGroup, userId, time);
                    });
                    thread.start();
                }

                executeWithoutDelete(ownerChatId,
                        "Пользователь " + userName + ", " + userId
                                + " замьючен на 3 часа во всех группах");
                return;
            }
        }
        pretendedMute.put(userId, LocalDateTime.now());
    }
    private final RestrictChatMember member = new RestrictChatMember();
    private void restrictUser(Long chatId, Long userId, int time) {
        member.setChatId(chatId);
        member.setUserId(userId);

        ChatPermissions permissions = new ChatPermissions();
        permissions.setCanSendMessages(false);
        member.setPermissions(permissions);
        member.setUntilDate(time);

        try {
            executeAsync(member);
        } catch (TelegramApiException e) {
        }
    }
    private void sendThisMessage(Long ownerChatId, Message message, String badWord) {

        try {
            String group = "@" + message.getChat().getUserName();
            String userName = "@" + message.getFrom().getUserName();

            if (!message.hasPhoto()) {
                SendMessage msg = new SendMessage();
                msg.setChatId(ownerChatId);
                msg.setText(userName + "\n" + group + "\n" + message.getText() + "\n\nПричина: " + badWord);
                msg.setEntities(message.getEntities());
                executeAsync(msg);
                return;
            }

            if (message.hasPhoto()) {
                SendPhoto msg = new SendPhoto();
                msg.setChatId(ownerChatId);
                msg.setPhoto(new InputFile(message.getPhoto().get(0).getFileId()));
                msg.setCaption(userName + "\n" + group + "\n" + message.getCaption() + "\n\nПричина: " + badWord);
                msg.setCaptionEntities(message.getCaptionEntities());
                executeAsync(msg);
            }

        } catch (Exception e) {
            SendMessage msg = new SendMessage();
            msg.setChatId(ownerChatId);
            msg.setText("@" + message.getFrom().getUserName() + "\nВложение не удалось загрузить.\nТекст:" +
                    message.getCaption() + "\n\nПричина: " + badWord);
            try {
                executeAsync(msg);
            } catch (TelegramApiException ex) {
            }
        }
    }
    private void sendListStopWords(Long chatId) {
        List<StopWordObject> s = stopWordService.getAllStopWordsByOwnerChatId(chatId);
        if (!s.isEmpty()) {

            s.sort(Comparator.comparing(StopWordObject::getWord));
            int batchSize = 60;
            for (int startIndex = 0; startIndex < s.size(); startIndex += batchSize) {
                int endIndex = Math.min(startIndex + batchSize, s.size());
                List<StopWordObject> batch = s.subList(startIndex, endIndex);

                executeMsg(stopWordMessage.getListWords(builder,chatId, batch));
            }
        } else {
            executeMsg(stopWordMessage.theListIsEmpty(builder,chatId));
        }
    }
    private void callBackDataHandle(Update update) {
        Long chatId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();

        clearWaitList(chatId);
        startMenuHandler(update, chatId, data);
        stopWordHandler(chatId, data);
        advertisersHandler(update, chatId, data);
        vipUsersHandler(chatId, data);
    }
    private void startMenuHandler(Update update, Long chatId, String data) {
        try {
            int i = Integer.parseInt(data);

            if (i == 0) {
                deleteMessage(chatId);
                executeMsg(startMessage.getStopWordMainMenu(builder,chatId));
                return;
            }

            if (i == 3) {
                deleteMessage(chatId);
                executeMsg(startMessage.getAdminsMainMenu(builder,chatId));
                return;
            }

            if (i == 6) {
                deleteMessage(chatId);
                executeMsg(startMessage.getGroupsMainMenu(builder,chatId));
            }

        } catch (Exception e) {

            if (data.equals("addMyNewGroup")) {
                deleteMessage(chatId);

                waitForNewOwnerGroup.add(chatId);
                executeMsg(ownersGroupsMessage.getEnterNewGroupUserName(builder,chatId));
                return;
            }

            if (data.equals("myGroupList")) {
                deleteMessage(chatId);

                executeMsg(ownersGroupsMessage.getMyGroupList(builder,chatId));
                return;
            }

            if (data.equals("backToStart")) {
                deleteMessage(chatId);
                if (chatId.equals(getOwnerChatId())) {
                    executeMsg(godMessage.start(builder, getOwnerChatId()));
                    return;
                }
                executeMsg(startMessage.getMainClientMenu(builder,chatId, clients.get(chatId).isCheckPhoto()));
                return;
            }

            if (data.contains("getGroups_")) {
                deleteMessage(chatId);
                int index = Integer.parseInt(data.split("_")[1]);
                executeMsg(advMessage.getGroupsKeyList(builder,chatId, createNewAdvUser.get(chatId).getUserName(), index));
            }

            if (data.contains("setG:")) {
                deleteMessage(chatId);
                String group = data.split(":")[1];
                createNewAdvUser.get(chatId).setPermissionToGroup(group);
                executeMsg(advMessage.addCountPostKeys(builder,chatId, createNewAdvUser.get(chatId)));
            }

            if (data.contains("dayCount_")) {
                deleteMessage(chatId);
                AdvUser advUser = advHandler.setDays(data, createNewAdvUser.get(chatId));
                if (advUser != null) {
                    executeMsg(advMessage.dataSaved(builder,chatId, advUser));
                    createNewAdvUser.put(chatId, advHandler.createNew(update, chatId));
                    init();
                    return;
                }
                executeWithoutDelete(chatId, "Что-то пошло не так! Попробуйте снова!");
                executeMsg(advMessage.addCountPostKeys(builder,chatId, createNewAdvUser.get(chatId)));
            }

            if (data.equals("clientList")
                    && chatId.equals(getOwnerChatId())) {
                deleteMessage(chatId);
                executeMsg(godMessage.clientsList(builder, getOwnerChatId()));
            }
        }
    }
    private void stopWordHandler(Long chatId, String data) {
        try {
            int i = Integer.parseInt(data);

            if (i == 4) {
                deleteMessage(chatId);
                sendListStopWords(chatId);
                return;
            }

            if (i == 5) {
                deleteMessage(chatId);
                enterNewStopWord.add(chatId);
                executeMsg(stopWordMessage.enterNewStopWord(builder,chatId));
                init();
            }

        } catch (Exception e) {}
    }

    private void advertisersHandler(Update update, Long chatId, String data) {
        try {
            int i = Integer.parseInt(data);

            if (i == 13) {
                deleteMessage(chatId);
                createNewAdvUser.remove(chatId);
                executeMsg(advMessage.getAdvertisersStart(builder,chatId));
            }

            if (i == 14) {
                deleteMessage(chatId);
                createNewAdvUser.put(chatId, advHandler.createNew(update, chatId));
                executeMsg(advMessage.addAdvertiser(builder,chatId));
            }

            if (i == 15) {
                deleteMessage(chatId);
                executeMsg(advMessage.getListUsersPermission(builder,chatId));
            }

        } catch (Exception e) {

        }
    }

    private void vipUsersHandler(Long chatId, String data) {
        try {
            int i = Integer.parseInt(data);

            if (i == 88) {
                deleteMessage(chatId);
                waitForNewVipUser.remove(chatId);
                executeMsg(adminMessage.vipUsersMainMenu(builder,chatId));
                return;
            }

            if (i == 87) {
                deleteMessage(chatId);
                waitForNewVipUser.add(chatId);
                executeMsg(adminMessage.addVipUsers(builder,"", chatId));
            }

            if (i == 86) {
                deleteMessage(chatId);
                waitForNewVipUser.remove(chatId);
                executeMsg(adminMessage.getVipListUser(builder,chatId));
            }

        } catch (Exception e) {}
    }

    private void userClientHandler(Update update) {
        Long chatId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();


        if (data.equals("5day")) {
            deleteMessage(chatId);
            executeMsg(userMessage.payUpdate(builder,chatId, 2));
            clients.put(chatId, userClientService.findByChatId(chatId).get());
            executeMsg(startMessage.getMainClientMenu(builder,chatId, clients.get(chatId).isCheckPhoto()));
            executeMsg(userMessage.fiveDaysTryMsg(builder, getOwnerChatId(), clients.get(chatId)));
            return;
        }

        if (data.contains("pay_")) {
            String[] payData = data.split("_");
            Long chatUserId = Long.valueOf(payData[1]);

            if (payData.length == 2) {
                executeMsg(userMessage.payUpdate(builder,chatUserId, 1));
                clients.put(chatUserId, userClientService.findByChatId(chatUserId).get());
                executeMsg(startMessage.getMainClientMenu(builder,chatUserId, clients.get(chatUserId).isCheckPhoto()));
            } else {
                executeMsg(userMessage.payUpdate(builder,chatUserId, 0));
            }
            editKeyboard(chatUserId);
            return;
        }

        if (data.equals("sendPayScreen")) {
            deleteMessage(chatId);
            waitForScreenShot.add(chatId);
            executeMsg(userMessage.waitForScreenShot(builder,chatId));
            return;
        }

        if (data.equals("letsReg")) {
            deleteMessage(chatId);
            String userClientName = update.getCallbackQuery().getFrom().getUserName();
            UserClient user = userClientService.getNewClient(chatId, userClientName);
            userClientService.save(user);
            executeMsg(userMessage.pretendedToPay(builder,chatId));
            return;
        }

        if (data.equals("switchPhoto")) {
            deleteMessage(chatId);
            clients.get(chatId).setCheckPhoto(!clients.get(chatId).isCheckPhoto());
            userClientService.save(clients.get(chatId));
            executeMsg(startMessage.getMainClientMenu(builder,chatId, clients.get(chatId).isCheckPhoto()));
        }
    }

    private void textMessageHandle(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        if (text.equals("/start")) {

            if (getOwnerChatId().equals(chatId)) {
                executeMsg(godMessage.start(builder, chatId));
                return;
            }

            if (clients.get(chatId) != null && clients.get(chatId).isPaid() && clients.get(chatId).isTryPeriod()) {
                clearWaitList(chatId);
                executeMsg(startMessage.getMainClientMenu(builder,chatId, clients.get(chatId).isCheckPhoto()));
                return;
            }

            if (clients.get(chatId) != null && !clients.get(chatId).isPaid() && clients.get(chatId).isTryPeriod()) {
                clearWaitList(chatId);
                executeMsg(userMessage.payIsOff(builder,clients.get(chatId)));
                return;
            }

            executeMsg(userMessage.getStartMessage(builder,chatId));
        }

        if (text.contains("/deleteClient_")
                && chatId.equals(getOwnerChatId())) {
            executeMsg(godMessage.deleteClient(builder, getOwnerChatId(), text));
            init();
        }

        if (text.contains("/deleteStopWord_")) {

            if (stopWordHandler.deleteTheWord(text)) {
                executeMsg(stopWordMessage.deletedSuccess(builder,chatId));
                init();

            } else {
                executeWithoutDelete(chatId, "Что-то пошло не так! Попробуйте снова!");
            }
            return;
        }

        if (text.contains("/deletePartner_")) {
            try {
                int id = Integer.parseInt(text.trim().split("_")[1]);

                if (advService.deleteByUserId(chatId, id)) {
                    executeMsg(advMessage.getListUsersPermission(builder,chatId));
                } else {
                    executeMsg(advMessage.getListUsersPermission(builder,chatId));
                    executeWithoutDelete(chatId, "Что-то пошло не так! Попробуйте снова!");
                }
                init();
                return;
            } catch (Exception e) {

            }
        }

        if (text.contains("/addOnePost_")) {
            editAdvertisersPostsCount(chatId, text, 1);
        }

        if (text.contains("/removeOnePost_")) {
            editAdvertisersPostsCount(chatId, text, (-1));
        }

        if (text.contains("/deleteVip_")) {
            int id  = Integer.parseInt(text.split("_")[1]);
            if (vipService.delete(chatId, id)) {
                init();
                executeWithoutDelete(chatId, "username удален из VIP-списка");
            } else {
                executeWithoutDelete(chatId, "Что-то пошло не так");
            }
            return;
        }

        if (text.contains("/deleteMyGroup_")) {
            int id  = Integer.parseInt(text.split("_")[1]);
            if (groupListenService.delete(chatId, id)) {
                init();
                executeWithoutDelete(chatId, "Группа удалена из списка");
            } else {
                executeWithoutDelete(chatId, "Что-то пошло не так");
            }
            return;
        }

        if (waitForNewVipUser.contains(chatId)) {

            if (vipUserHandle.addNewVipUser(chatId,text)) {
                executeMsg(adminMessage.addVipUsers(builder,"Данные сохранены!\n", chatId));
                init();
                return;
            }
            executeMsg(adminMessage.addVipUsers(builder,"Что-то пошло не так", chatId));
            return;
        }

        if (enterNewStopWord.contains(chatId)) {

            if (stopWordHandler.saveTheWord(chatId, text)) {
                executeWithoutDelete(chatId, "Cохраненo! Можно добавить еще.");
                executeMsg(stopWordMessage.enterNewStopWord(builder,chatId));
                init();
            } else {
                executeMsg(startMessage.getSendMessage(chatId,
                        text + "Что-то пошло не так! Попробуйте снова!", null));
                executeMsg(stopWordMessage.enterNewStopWord(builder,chatId));
            }

            return;
        }

        if (waitForNewOwnerGroup.contains(chatId)) {
            executeMsg(ownersGroupsMessage.getMsgAboutSaveNewGroup(builder,chatId,
                    groupListenService.save(chatId, text)));
            return;
        }

        if (createNewAdvUser.containsKey(chatId)) {
            if (text.contains("@")) {
                createNewAdvUser.get(chatId).setUserName(text.trim());
                executeMsg(advMessage.getGroupsKeyList(builder,chatId, text, 0));
                return;
            }
            executeMsg(advMessage.somethingWentWrong(builder,chatId));
        }
    }

    private void editAdvertisersPostsCount(Long chatId, String text, int count) {
        int id = Integer.parseInt(text.split("_")[1]);
        AdvUser user = advService.getUserById(id);
        advService.deleteByUserId(chatId, id);
        user.setId(null);
        user.setPostCount(user.getPostCount() + count);
        advService.save(user);

        executeMsg(advMessage.getListUsersPermission(builder,chatId));
        init();
    }
    private void executeWithoutDelete(Long chatId, String text) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
        }
    }
    private void executeMsg(SendMessage msg) {
        if (!messageToDelete.isEmpty()) {
            Long chatId = Long.valueOf(msg.getChatId());

            if (messageToDelete.containsKey(chatId)) {
                for (Integer i : messageToDelete.get(chatId)) {
                    DeleteMessage d = new DeleteMessage();
                    d.setChatId(chatId);
                    d.setMessageId(i);

                    try {
                        execute(d);
                    } catch (TelegramApiException e) {}
                }
                messageToDelete.remove(chatId);
            }
        }

        String text = msg.getText();
        int chunkSize = 4000;

        if (text.length() > chunkSize) {
            int numChunks = (int) Math.ceil((double) text.length() / chunkSize);

            for (int i = 0; i < numChunks; i++) {
                int start = i * chunkSize;
                int end = Math.min((i + 1) * chunkSize, text.length());
                String chunk = text.substring(start, end);

                SendMessage chunkMsg = new SendMessage();
                chunkMsg.setChatId(msg.getChatId());
                chunkMsg.setText(chunk);
                chunkMsg.setReplyMarkup(msg.getReplyMarkup());

                try {
                    int msgId = execute(chunkMsg).getMessageId();
                    Long chatIdChunkMsg = Long.valueOf(chunkMsg.getChatId());

                    if (messageToDelete.containsKey(chatIdChunkMsg)) {
                        messageToDelete.get(chatIdChunkMsg).add(msgId);
                    } else {
                        List<Integer> list = new ArrayList<>();
                        list.add(msgId);
                        messageToDelete.put(chatIdChunkMsg, list);
                    }
                } catch (TelegramApiException e) {

                }
            }

        } else {
            try {
                chatIdMsgId.put(Long.valueOf(msg.getChatId()), execute(msg).getMessageId());
            } catch (TelegramApiException e) {

            }
        }
    }
    public void deleteMessage(Long chatId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(chatIdMsgId.get(chatId));
        try {
            execute(deleteMessage);
        } catch (Exception e) {
        }
    }
    private final DeleteMessage delete = new DeleteMessage();
    private void deleteBadMessage(Long chatId, int messageId) {
        delete.setChatId(chatId);
        delete.setMessageId(messageId);
        try {
            execute(delete);
        } catch (TelegramApiException e) {
        }
    }
    private void editKeyboard(Long chatUserId) {
        EditMessageReplyMarkup edit = new EditMessageReplyMarkup();
        edit.setChatId(getOwnerChatId());
        edit.setMessageId(sendApproveMsg.get(chatUserId));
        edit.setReplyMarkup(null);

        try {
            execute(edit);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    private void clearWaitList(Long chatId) {
        enterNewStopWord.remove(chatId);

        waitForNewOwnerGroup.remove(chatId);
        waitForNewVipUser.remove(chatId);
    }
}