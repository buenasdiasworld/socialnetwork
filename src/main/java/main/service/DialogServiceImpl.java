package main.service;

import lombok.AllArgsConstructor;
import main.core.ContextUtilities;
import main.core.OffsetPageRequest;
import main.data.request.DialogAddRequest;
import main.data.request.DialogMessageRequest;
import main.data.request.ListRequest;
import main.data.response.base.ListResponse;
import main.data.response.base.Response;
import main.data.response.type.*;
import main.exception.BadRequestException;
import main.exception.apierror.ApiError;
import main.model.*;
import main.repository.BlocksBetweenUsersRepository;
import main.repository.DialogRepository;
import main.repository.MessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DialogServiceImpl implements DialogService {
    private final DialogRepository dialogRepository;
    private final MessageRepository messageRepository;
    private final PersonService personService;
    private final NotificationService notificationService;
    private final BlocksBetweenUsersRepository blocksBetweenUsersRepository;

    @Override
    public ListResponse<DialogList> list(ListRequest request) {
        Person currentPerson = ContextUtilities.getCurrentPerson();
        int currentUserId = currentPerson.getId();

        List<DialogList> dialogs = new ArrayList<>();
        Pageable pageable;
        Page<Dialog> page;

        if (request.getItemPerPage() > 0) {
            pageable = new OffsetPageRequest(request.getOffset(), request.getItemPerPage(), Sort.unsorted());
        } else {
            pageable = Pageable.unpaged();
        }

        page = dialogRepository.findByPersons_id(currentUserId, pageable);

        page.forEach(i -> {
            DialogList item = new DialogList(i);

            item.setUnreadCount(messageRepository.countByReadStatusAndRecipient_idAndDialog_id(
                    ReadStatus.SENT,
                    currentPerson.getId(),
                    item.getId()
            ));

            Message lastMessage = messageRepository.findTopByDialog_idOrderByTimeDesc(item.getId());
            if (lastMessage != null) {
                DialogMessage dialogMessage = new DialogMessage(lastMessage);
                item.setLastMessage(dialogMessage);
            }

            dialogs.add(item);
        });

        return new ListResponse<>(
                dialogs,
                page.getTotalElements(),
                request.getOffset(),
                request.getItemPerPage()
        );
    }

    @Override
    public Response<DialogNew> add(DialogAddRequest request) {
        Person currentPerson = ContextUtilities.getCurrentPerson();

        Dialog dialog = null;
        boolean isGroupDialog = request.getUserIds().size() > 1;

        //Может быть диалог тет-а-тет уже есть, проверим
        if (!isGroupDialog) {
            dialog = dialogRepository.findTetATet(
                    currentPerson.getId(),
                    request.getUserIds().get(0)
            );
        }

        if (dialog == null) {
            dialog = new Dialog();

            List<Person> persons = request.getUserIds().stream().map(personService::getById).collect(Collectors.toList());
            persons.add(personService.getById(currentPerson.getId()));

            dialog.setPersons(persons);

            if (request.getName() != null && !request.getName().isEmpty()) {
                dialog.setName(request.getName());
            } else {
                if (isGroupDialog) {
                    dialog.setName("Групповая беседа");
                }
            }

            dialog.setGroup(isGroupDialog);

            dialogRepository.save(dialog);
        }

        return new Response<>(new DialogNew(dialog.getId()));
    }

    @Override
    public Response<DialogMessage> addMessage(int dialogId, DialogMessageRequest request) {
        Response<DialogMessage> response = new Response<>();
        Dialog dialog = dialogRepository.findById(dialogId);

        List<Person> personsInDialog = dialog.getPersons();
        Person currentPerson = ContextUtilities.getCurrentPerson();
        Person anotherPersonInDialog = getAntherPersonInDialog(personsInDialog, currentPerson);
        BlocksBetweenUsers blocksBetweenUsers = blocksBetweenUsersRepository
                .findBySrc_IdAndDst_Id(anotherPersonInDialog != null ? anotherPersonInDialog.getId() : 0, currentPerson.getId());

        if (blocksBetweenUsers == null) {
            dialog.getPersons().forEach(p -> {
                Message message = new Message();
                message.setMessageText(request.getMessageText());
                message.setDialog(dialog);
                message.setTime(Instant.now());
                message.setAuthor(currentPerson);
                message.setRecipient(p);
                message.setReadStatus((p.getId() != currentPerson.getId()) ? ReadStatus.SENT : ReadStatus.READ);
                messageRepository.save(message);
                // отправляем уведомление только при отправке сообщения
                if (message.getReadStatus().equals(ReadStatus.SENT)) {
                    notificationService.setNotification(message);
                }
            });

            DialogMessage dialogMessage = new DialogMessage();
            dialogMessage.setMessageText(request.getMessageText());
            response.setData(dialogMessage);
            return response;
        } else {
            throw new BadRequestException(new ApiError("Access blocked", "Отправка сообщений заюлокирована"));
        }
    }

    @Override
    public ListResponse<DialogMessage> listMessage(int dialogId, ListRequest request) {
        int currentUserId = ContextUtilities.getCurrentUserId();

        List<DialogMessage> messages = new ArrayList<>();
        Pageable pageable;
        Page<Message> page;

        if (request.getItemPerPage() > 0) {
            pageable = new OffsetPageRequest(request.getOffset(), request.getItemPerPage(), Sort.by(Sort.Order.desc("id")));
        } else {
            pageable = Pageable.unpaged();
        }

        page = messageRepository.findByDialog_idAndRecipient_id(dialogId, currentUserId, pageable);

        page.forEach(i -> {
            DialogMessage item = new DialogMessage(i);
            messages.add(item);
        });

        messages.stream().filter(msg -> msg.getReadStatus().equals(ReadStatus.SENT)).forEach(msg -> setReadMessage(msg.getId()));

        return new ListResponse<>(
                messages,
                page.getTotalElements(),
                request.getOffset(),
                request.getItemPerPage()
        );
    }

    @Override
    public Response<ResponseCount> countUnreadedMessage() {
        long count = messageRepository.countByReadStatusAndRecipient_id(
                ReadStatus.SENT,
                ContextUtilities.getCurrentUserId()
        );

        return new Response<>(new ResponseCount(count));
    }

    @Override
    public Response<ResponseMessage> setReadMessage(int messageId) {
        Message message = messageRepository.findById(messageId);
        message.setReadStatus(ReadStatus.READ);
        messageRepository.save(message);

        return new Response<>(new ResponseMessage("ok"));
    }

    @Override
    public Message findById(int id) {
        return messageRepository.findById(id);
    }

    private Person getAntherPersonInDialog(List<Person> persons, Person currentPerson) {
        for (Person person : persons) {
            if (person.getId() != currentPerson.getId()) {
                return person;
            }
        }
        return null;
    }

}
