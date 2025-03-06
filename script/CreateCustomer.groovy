import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityCondition
import org.moqui.entity.EntityFind
import org.moqui.entity.EntityList
import org.moqui.entity.EntityValue

import java.sql.Timestamp

def currentDate = new Timestamp(System.currentTimeMillis())

EntityFind ef = ec.entity.find('Moqui.Party.FindCustomerView')
ef.selectFields()

ef.condition("infoString", emailAddress)
EntityValue el=ef.one()

if (el == null) {
    def findEntity = ec.entity.makeValue('Party')
    findEntity.setSequencedIdPrimary()
    findEntity.set('partyTypeEnumId', 'Person')
    findEntity.create()

    def pid = findEntity.get('partyId')

    def partyroleEntity = ec.entity.makeValue('PartyRole')
    partyroleEntity.set('partyId',pid)
    partyroleEntity.set('roleTypeEnumId','Customer')
    partyroleEntity.create()
    
    def personEntity = ec.entity.makeValue('Person')
    personEntity.set('partyId', pid)
    personEntity.set('firstName', firstName)
    personEntity.set('lastName', lastName)
    personEntity.create()

    def contactMechEntity = ec.entity.makeValue('ContactMech')
    contactMechEntity.setSequencedIdPrimary()
    contactMechEntity.set('infoString', emailAddress)
    contactMechEntity.create()

    def contactMechId = contactMechEntity.get('contactMechId')

    def partyContactMechentity = ec.entity.makeValue('PartyContactMech')
    partyContactMechentity.set('partyId', pid)
    partyContactMechentity.set('contactMechId', contactMechId)
    partyContactMechentity.set('contactMechPurposeEnumId', 'EmailPrimary')
    partyContactMechentity.set('fromDate', currentDate)
    partyContactMechentity.create()

    context.datalist=pid
}
