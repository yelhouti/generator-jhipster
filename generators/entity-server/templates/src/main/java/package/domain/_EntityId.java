<%#
 Copyright 2013-2018 the original author or authors from the JHipster project.

 This file is part of the JHipster project, see http://www.jhipster.tech/
 for more information.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-%>
package <%=packageName%>.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;
<%_ idClass=entityClass+"Id"; _%>
<%_ idInstance= idClass.charAt(0).toLowerCase().concat(idClass.slice(1)); _%>
<%_
idFields = []
for (idx in relationships){
    if(relationships[idx].primaryKey){
        let field={};
        //TODO set field from relashionship
        field.fieldType = "Long"
        field.fieldName = relationships[idx].relationshipName+"Id"
        field.fieldNameAsDatabaseColumn = getColumnName(relationships[idx].relationshipName)+"_id"
        idFields.push(field);
    }
}
for (idx in fields){
    if(fields[idx].primaryKey){
        idFields.push(fields[idx]);
    }
}
_%>

@Embeddable
public class <%= idClass %> implements java.io.Serializable {
    <%
    for (idx in idFields) {
    %>
    @Column(name = "<%= idFields[idx].fieldNameAsDatabaseColumn %>", nullable = false)
    private <%= idFields[idx].fieldType %> <%= idFields[idx].fieldName %>;
    <% } %>
<%_
let constructorParams="";
for (idx in idFields) {
    constructorParams+=idFields[idx].fieldType+" "+idFields[idx].fieldName+", ";
}
constructorParams=constructorParams.slice(0,-2);
_%>
    public <%= idClass %>(){}
    public <%= idClass %>(<%= constructorParams %>){
<%_ for (idx in idFields) { _%>
        this.<%= idFields[idx].fieldName %>=<%= idFields[idx].fieldName %>;
<%_ } _%>
    }

<%_ for (idx in idFields) {
    const fieldType = idFields[idx].fieldType;
    const fieldName = idFields[idx].fieldName;
    const fieldInJavaBeanMethod = fieldName.charAt(0).toUpperCase().concat(fieldName.slice(1)); _%>
    public <%= fieldType %> <% if (fieldType.toLowerCase() === 'boolean') { %>is<% } else { %>get<%_ } _%><%= fieldInJavaBeanMethod %>() {
        return <%= fieldName %>;
    }

    public void set<%= fieldInJavaBeanMethod %>(<%= fieldType %> <%= fieldName %>) {
        this.<%= fieldName %> = <%= fieldName %>;
    }

<%_ } _%>

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        <%= idClass %> <%= idInstance %> = (<%= idClass %>) o;
<%_ for (idx in idFields) { _%>
        <% if(idx==0){ %>return <% }else{ %>    && <% } %>Objects.equals(<%= idFields[idx].fieldName %>, businessBasicIndexId.<%= idFields[idx].fieldName %>)
<%_ } _%>
        ;
    }

    @Override
    public int hashCode() {
        int result = 17;
<%_ for (idx in idFields) { _%>
        result = 31 * result + <%= idFields[idx].fieldName %>.hashCode();
<%_ } _%>
        return result;
    }


}
