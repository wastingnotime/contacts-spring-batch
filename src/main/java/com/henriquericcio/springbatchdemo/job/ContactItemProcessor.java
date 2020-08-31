package com.henriquericcio.springbatchdemo.job;

import com.henriquericcio.springbatchdemo.model.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class ContactItemProcessor implements ItemProcessor<Contact, Contact> {
    private static final Logger log = LoggerFactory.getLogger(ContactItemProcessor.class);

    @Override
    public Contact process(final Contact contact) {

        final Contact transformedContact = new Contact(contact.getId(), contact.getLastName().toUpperCase(), contact.getFirstName().toUpperCase(), contact.getPhoneNumber());

        log.info("Converting (" + contact + ") into (" + transformedContact + ")");

        return transformedContact;
    }
}
