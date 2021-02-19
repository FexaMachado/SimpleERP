package controller;

import domain.Contact;
import repository.ContactRepository;
import ui.Main;
import util.DataRefresher;

import java.util.List;

public class ContactController {

    private ContactRepository repo;

    public ContactController() {
        this.repo = new ContactRepository(Main.getEntityManagerFactory());
    }

    public List<Contact> getContacts() {
        return repo.getAll();
    }

    public void deleteContact(Contact c) {
        repo.delete(c);
    }

    public void addContact(Contact c) {
        repo.add(c);
    }

    public void changeContactName(Contact c, String name) {
        String oldName = c.getName();
        c.setName(name);
        try {
            repo.update(c);
            DataRefresher.fireEvent(DataRefresher.Type.CONTACT);
        } catch (Exception e) {
            c.setName(oldName);
            throw new IllegalArgumentException("Não pode haver contactos com o mesmo nome");
        }
    }

    public void changeContactAddress(Contact c, String address) {
        c.setAddress(address);
        repo.update(c);
    }

    public void changeContactTelephone(Contact c, String telephone) {
        c.setTelephone(telephone);
        repo.update(c);
    }

    public void changeContactEmail(Contact c, String email) {
        c.setEmail(email);
        repo.update(c);
    }

    public void changeContactProvider(Contact c){
        c.setProvider(!c.isProvider());
        repo.update(c);
    }

}
