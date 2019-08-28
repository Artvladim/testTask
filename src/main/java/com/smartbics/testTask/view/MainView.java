package com.smartbics.testTask.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.smartbics.testTask.components.ClientEditor;
import org.springframework.beans.factory.annotation.Autowired;
import com.smartbics.testTask.repository.ClientRepository;
import com.smartbics.testTask.dao.ClientDao;


@Route
public class MainView extends VerticalLayout {
    private final ClientRepository clientRepository;

    private Grid<ClientDao> grid = new Grid<>(ClientDao.class);

    private final TextField filter = new TextField("","Type to filter");
    private final Button addNewBtn = new Button("Add new");
    private final HorizontalLayout toolbar = new HorizontalLayout(filter, addNewBtn);

    private final ClientEditor editor;


    @Autowired
    public MainView(ClientRepository clientRepository, ClientEditor editor) {
        this.clientRepository = clientRepository;
        this.editor = editor;

        add(toolbar,grid,editor);

        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> showClient(e.getValue()));

        // Connect selected Customer to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> {
            editor.editClient(e.getValue());
        });

        // Instantiate and edit new Customer the new button is clicked
        addNewBtn.addClickListener(e -> editor.editClient(new ClientDao()));

        // Listen changes made by the editor, refresh data from backend
        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            showClient(filter.getValue());
        });


        showClient("");
    }

    private void showClient(String name) {
        if (name.isEmpty()) {
            grid.setItems(clientRepository.findAll());
        } else {
            grid.setItems(clientRepository.findByName(name));
        }

    }
}
