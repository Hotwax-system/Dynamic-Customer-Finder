import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityCondition
import org.moqui.entity.EntityFind
import org.moqui.entity.EntityList
import org.moqui.entity.EntityValue
import java.sql.Timestamp

ExecutionContext ec = context.ec
def currentDate = Timestamp.valueOf(ec.user.nowTimestamp.toLocalDateTime().toLocalDate().atStartOfDay())

EntityFind ef = ec.entity.find('Moqui.Party.FindCustomerView')
ef.selectFields()

if (emailAddress) {
    ef.condition('infoString', EntityCondition.EQUALS, emailAddress)
}

EntityValue ev = ef.one()

if (ev != null) {
    def partyId = ev.partyId
    
    // Update Postal Address thruDate
    def postalList = ec.entity.find('PartyContactMech')
        .condition('partyId', partyId)
        .condition('contactMechPurposeEnumId', 'PostalPrimary')
        .condition('thruDate', null)
        .list()

    for (EntityValue postal in postalList) {
        postal.set('thruDate', currentDate)
        postal.update()
    }

    // Update Phone thruDate
    def phoneList = ec.entity.find('PartyContactMech')
        .condition('partyId', partyId)
        .condition('contactMechPurposeEnumId', 'PhonePrimary')
        .condition('thruDate', null)
        .list()

    for (EntityValue phone in phoneList) {
        phone.set('thruDate', currentDate)
        phone.update()
    }

    // Insert New Phone ContactMech
    def contactMechEntity = ec.entity.makeValue('ContactMech')
    contactMechEntity.setSequencedIdPrimary()
    contactMechEntity.create()

    def contactMechIdPhone = contactMechEntity.contactMechId

    def partyContactMechEntity = ec.entity.makeValue('PartyContactMech')
    partyContactMechEntity.set('partyId', partyId)
    partyContactMechEntity.set('contactMechId', contactMechIdPhone)
    partyContactMechEntity.set('contactMechPurposeEnumId', 'PhonePrimary')
    partyContactMechEntity.set('fromDate', currentDate)
    partyContactMechEntity.create()

    def telecomNumberEntity = ec.entity.makeValue('TelecomNumber')
    telecomNumberEntity.set('contactMechId', contactMechIdPhone)
    telecomNumberEntity.set('contactNumber', contactNumber)
    telecomNumberEntity.create()

    // Insert New Postal ContactMech
    def contactMechEntityPostal = ec.entity.makeValue('ContactMech')
    contactMechEntityPostal.setSequencedIdPrimary()
    contactMechEntityPostal.create()

    def contactMechIdPostal = contactMechEntityPostal.contactMechId

    def partyContactMechEntityPostal = ec.entity.makeValue('PartyContactMech')
    partyContactMechEntityPostal.set('partyId', partyId)
    partyContactMechEntityPostal.set('contactMechId', contactMechIdPostal)
    partyContactMechEntityPostal.set('contactMechPurposeEnumId', 'PostalPrimary')
    partyContactMechEntityPostal.set('fromDate', currentDate)
    partyContactMechEntityPostal.create()

    def postalAddressEntity = ec.entity.makeValue('PostalAddress')
    postalAddressEntity.set('contactMechId', contactMechIdPostal)
    postalAddressEntity.set('address1', address1)
    postalAddressEntity.set('address2', address2)
    postalAddressEntity.set('city', city)
    postalAddressEntity.set('postalCode', postalCode)
    postalAddressEntity.set('state', state)
    postalAddressEntity.create()

} else {
    ec.message.addError('Email not exists')
}
