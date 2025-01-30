import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityCondition
import org.moqui.entity.EntityFind
import org.moqui.entity.EntityList
import org.moqui.entity.EntityValue
import java.sql.Timestamp

def currentDate = new Timestamp(System.currentTimeMillis())

ExecutionContext ec = context.ec

EntityFind ef = ec.entity.find('Moqui.Party.FindCustomerView')

ef.selectFields()
if (emailAddress) {
    ef.condition('infoString', emailAddress)
}

EntityValue ev = ef.one()

if (ev != null) {
    EntityValue partyContactMechEntityu = ec.entity.find('PartyContactMech')
        .condition('contactMechId', ev.contactMechId)
        .one()

    if (partyContactMechEntityu != null) {
        partyContactMechEntityu.set('thruDate', currentDate)
        partyContactMechEntityu.update()
    }

    def contactMechId = contactMechEntity.get('contactMechId')
    def partyId = ev.partyId

    def partyContactMechEntity = ec.entity.makeValue('PartyContactMech')
    partyContactMechEntity.set('partyId', partyId)
    partyContactMechEntity.set('contactMechId', contactMechId)
    partyContactMechEntity.set('contactMechPurposeEnumId', 'EmailPrimary')
    partyContactMechEntity.set('fromDate', currentDate)
    partyContactMechEntity.create()

    def postalAddressEntity = ec.entity.makeValue('PostalAddress')
    postalAddressEntity.set('contactMechId', contactMechId)
    postalAddressEntity.set('address1', address1)
    postalAddressEntity.set('address2', address2)
    postalAddressEntity.set('city', city)
    postalAddressEntity.set('postalCode', postalCode)
    postalAddressEntity.set('state', state)
    postalAddressEntity.create()

    def telecomNumberEntity = ec.entity.makeValue('TelecomNumber')
    telecomNumberEntity.set('contactMechId', contactMechId)
    telecomNumberEntity.set('contactNumber', contactNumber)
    telecomNumberEntity.create()
}

else
{
    ec.message.addError('Email not exists')
}
