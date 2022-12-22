package com.example.application.views.list;

import com.example.application.data.entity.Company;
import com.example.application.data.entity.User;
import com.example.application.data.entity.Status;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
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

public class UserForm extends FormLayout {
  private User user;

  TextField nickname = new TextField("NickName");

  EmailField email = new EmailField("Email");
  ComboBox<Company> company = new ComboBox<>("Company");
  Binder<User> binder = new BeanValidationBinder<>(User.class);

  Button save = new Button("Save");
  Button delete = new Button("Delete");
  Button close = new Button("Cancel");

  public UserForm(List<Company> companies) {
    addClassName("user-form");
    binder.bindInstanceFields(this);

    company.setItems(companies);
    company.setItemLabelGenerator(Company::getName);
    add(nickname,
        email,
        company,
        createButtonsLayout()); 
  }

  private HorizontalLayout createButtonsLayout() {
    save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
    close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

    save.addClickShortcut(Key.ENTER);
    close.addClickShortcut(Key.ESCAPE);

    save.addClickListener(event -> validateAndSave());
    delete.addClickListener(event -> fireEvent(new DeleteEvent(this, user)));
    close.addClickListener(event -> fireEvent(new CloseEvent(this)));


    binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

    return new HorizontalLayout(save, delete, close); 
  }

  public void setContact(User user) {
    this.user = user;
    binder.readBean(user);
  }

  private void validateAndSave() {
    try {
      binder.writeBean(user);
      fireEvent(new SaveEvent(this, user));
    } catch (ValidationException e) {
      e.printStackTrace();
    }
  }

  // Events
  public static abstract class ContactFormEvent extends ComponentEvent<UserForm> {
    private User user;

    protected ContactFormEvent(UserForm source, User user) {
      super(source, false);
      this.user = user;
    }

    public User getContact() {
      return user;
    }
  }

  public static class SaveEvent extends ContactFormEvent {
    SaveEvent(UserForm source, User user) {
      super(source, user);
    }
  }

  public static class DeleteEvent extends ContactFormEvent {
    DeleteEvent(UserForm source, User user) {
      super(source, user);
    }

  }

  public static class CloseEvent extends ContactFormEvent {
    CloseEvent(UserForm source) {
      super(source, null);
    }
  }

  public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                ComponentEventListener<T> listener) {
    return getEventBus().addListener(eventType, listener);
  }
}