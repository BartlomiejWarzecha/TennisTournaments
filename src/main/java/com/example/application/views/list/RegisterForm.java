package com.example.application.views.list;

import com.example.application.data.entity.Tournament;
import com.example.application.data.entity.User;
import com.example.application.data.entity.Status;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;

import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import java.util.List;

public class ContactForm extends FormLayout { 
  TextField firstName = new TextField("First name"); 
  TextField lastName = new TextField("Last name");
  TextField nickname = new TextField("Nickname");
  EmailField email = new EmailField("Email");
  ComboBox<Status> status = new ComboBox<>("Status");
  ComboBox<Tournament> tournament = new ComboBox<>("Tournament");
  private User user;

  Button save = new Button("Save");
  Button delete = new Button("Delete");
  Button close = new Button("Cancel");

  Binder<User> binder = new BeanValidationBinder<>(User.class);

  public ContactForm(List<Tournament> companies, List<Status> statuses) {

    addClassName("user-form");
    binder.bindInstanceFields(this);

    tournament.setItems(companies);
    tournament.setItemLabelGenerator(Tournament::getName);
    status.setItems(statuses);
    status.setItemLabelGenerator(Status::getName);


    add(firstName, 
        lastName,
        nickname,
        email,
        tournament,
        status,
        createButtonsLayout());
  }

  private HorizontalLayout createButtonsLayout() {
    save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
    close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

    save.addClickListener(event -> validateAndSave());
    delete.addClickListener(event -> fireEvent(new DeleteEvent(this, user)));
    close.addClickListener(event -> fireEvent(new CloseEvent(this)));

    binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
    return new HorizontalLayout(save, delete, close);
  }

  private void validateAndSave() {
    try {
      binder.writeBean(user);
      fireEvent(new SaveEvent(this, user));
    } catch (ValidationException e) {
      e.printStackTrace();
    }
  }

  public void setContact(User user) {
    this.user = user;
    binder.readBean(user);
  }

  public static abstract class ContactFormEvent extends ComponentEvent<ContactForm> {
    private User user;

    protected ContactFormEvent(ContactForm source, User user) {
      super(source, false);
      this.user = user;
    }

    public User getContact() {
      return user;
    }
  }

  public static class SaveEvent extends ContactFormEvent {
    SaveEvent(ContactForm source, User user) {
      super(source, user);
    }
  }

  public static class DeleteEvent extends ContactFormEvent {
    DeleteEvent(ContactForm source, User user) {
      super(source, user);
    }

  }

  public static class CloseEvent extends ContactFormEvent {
    CloseEvent(ContactForm source) {
      super(source, null);
    }
  }

  public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
      ComponentEventListener<T> listener) {
    return getEventBus().addListener(eventType, listener);

  }


}
