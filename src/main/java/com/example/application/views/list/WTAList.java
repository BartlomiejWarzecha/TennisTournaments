package com.example.application.views.list;

import com.example.application.data.entity.Contact;
import com.example.application.data.service.CrmService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Collections;
import javax.annotation.security.PermitAll;

@PermitAll
@Route(value="WTAPlayers", layout = MainLayout.class)
@PageTitle("WTA")
public class SecondListView extends VerticalLayout {
  Grid<Contact> grid = new Grid<>(Contact.class);
  TextField filterText = new TextField();
  PlayerFormWTA form;
  CrmService service;

  public SecondListView(CrmService service) {
    this.service = service;
    addClassName("second-list-view");
    setSizeFull();
    configureGrid();
    configureForm();

    add(getToolbar(), getContent());
    updateList();
    closeEditor();
  }

  private Component getContent() {
    HorizontalLayout content = new HorizontalLayout(grid, form);
    content.setFlexGrow(2, grid);
    content.setFlexGrow(1, form);
    content.addClassNames("content");
    content.setSizeFull();
    return content;
  }

  private void configureForm() {
    form = new PlayerFormWTA(service.findAllCompanies(), service.findAllStatuses());
    form.setWidth("25em");
    form.addListener(PlayerFormWTA.SaveEvent.class, this::saveContact);
    form.addListener(PlayerFormWTA.DeleteEvent.class, this::deleteContact);
    form.addListener(PlayerFormWTA.CloseEvent.class, e -> closeEditor());
  }

  private void saveContact(PlayerFormWTA.SaveEvent event) {
    service.saveContact(event.getContact());
    updateList();
    closeEditor();
  }

  private void deleteContact(PlayerFormWTA.DeleteEvent event) {
    service.deleteContact(event.getContact());
    updateList();
    closeEditor();
  }

  private void configureGrid() {
    grid.addClassNames("contact-grid");
    grid.setSizeFull();
    grid.setColumns("firstName", "lastName", "email");
    grid.addColumn(contact -> contact.getStatus().getName()).setHeader("Status");
    grid.addColumn(contact -> contact.getTournament().getName()).setHeader("Tournament");
    grid.getColumns().forEach(col -> col.setAutoWidth(true));

    grid.asSingleSelect().addValueChangeListener(event ->
        editContact(event.getValue()));
  }

  private HorizontalLayout getToolbar() {
    filterText.setPlaceholder("Filter by name...");
    filterText.setClearButtonVisible(true);
    filterText.setValueChangeMode(ValueChangeMode.LAZY);
    filterText.addValueChangeListener(e -> updateList());

    Button addContactButton = new Button("Add contact");
    addContactButton.addClickListener(click -> addContact());

    HorizontalLayout toolbar = new HorizontalLayout(filterText, addContactButton);
    toolbar.addClassName("toolbar");
    return toolbar;
  }
  public void editContact(Contact contact) {
    if (contact == null) {
      closeEditor();
    } else {
      form.setContact(contact);
      form.setVisible(true);
      addClassName("editing");
    }
  }

  private void closeEditor() {
    form.setContact(null);
    form.setVisible(false);
    removeClassName("editing");
  }

  private void addContact() {
    grid.asSingleSelect().clear();
    editContact(new Contact());
  }


  private void updateList() {
    grid.setItems(service.findAllContacts(filterText.getValue()));
  }
}
