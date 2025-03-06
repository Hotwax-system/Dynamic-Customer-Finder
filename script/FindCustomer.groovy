import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityCondition
import org.moqui.entity.EntityFind
import org.moqui.entity.EntityList
import org.moqui.entity.EntityValue

ExecutionContext ec = context.ec

EntityFind ef = ec.entity.find('Moqui.Party.FindCustomerView')

ef.selectFields()

def pageSize=10

if (emailAddress) { ef.condition('infoString', EntityCondition.EQUALS, emailAddress) }

if (firstName) { ef.condition('firstName', EntityCondition.LIKE, '%' + firstName + '%') }

if (lastName) { ef.condition('lastName', EntityCondition.LIKE, '%' + lastName + '%') }

if (contactNumber) { ef.condition('contactNumber', EntityCondition.EQUALS, contactNumber) }

if (postalAddress) { ef.condition('postalCode', EntityCondition.EQUALS, postalAddress) }

if(pageIndex){
ef.offset(pageIndex as int,pageSize as int)
ef.limit(pageSize as int)
}

datalist = []
ef.orderBy("firstName,lastName")
EntityList el = ef.list()
for (EntityValue ev in el) datalist.add(ev.partyId)








