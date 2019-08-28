package com.smartbics.testTask.components;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Setter;
import com.smartbics.testTask.dao.ClientDao;
import com.smartbics.testTask.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;


@SpringComponent
@UIScope
public class ClientEditor extends VerticalLayout implements KeyNotifier {
    private final ClientRepository clientRepository;

    private ClientDao client;

    private TextField id = new TextField("id");
    private TextField name = new TextField("name");
    private TextField age = new TextField("age");

    private Button save = new Button("Save", VaadinIcon.CHECK.create());
    private Button cancel = new Button("Cancel");
    private Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    private HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

    private Binder<ClientDao> binder = new Binder<>(ClientDao.class);
    @Setter
    private ChangeHandler changeHandler;
    public interface ChangeHandler {
        void onChange();
    }

    @Autowired
    public ClientEditor(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;

        add(id,name,age,actions);

        binder.bindInstanceFields(this);

        setSpacing(true);

        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");

        addKeyPressListener(Key.ENTER, e -> save());

        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> editClient(client));
        setVisible(false);
    }

    private void delete() {
        clientRepository.delete(client);
        changeHandler.onChange();
    }

    private void save(){
        clientRepository.save(client);
        changeHandler.onChange();
    }

    public void editClient(ClientDao newClient){
        if (newClient == null) {
            setVisible(false);
            return;
        }
        if(newClient.getId() != null) {
            client = clientRepository.findById(newClient.getId()).orElse(newClient);
        } else {
            client = newClient;
            }
        binder.setBean(client);

        setVisible(true);
        name.focus();
        }
    }

