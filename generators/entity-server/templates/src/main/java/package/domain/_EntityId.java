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

@Embeddable
public class <%= idClass %> implements java.io.Serializable {
    <%
    for (idx in id) {
        let field = fields.find(f=>id[idx].attributeName==f.fieldName);
        if(field != undefined){
            id[idx].type=field.fieldType;
        }else{
            id[idx].type="Long";
        }
    }
    for (idx in id) {
        let field=id[idx];
    %>
    @Column(name = "<%= field.columnName %>", nullable = false)
    private <%= field.type %> <%= field.attributeName %>;
    <% } %>
<%_
let constructorParams="";
for (idx in id) {
    constructorParams+=id[idx].type+" "+id[idx].attributeName+", ";
}
constructorParams=constructorParams.slice(0,-2);
_%>
    public <%= idClass %>(<%= constructorParams %>){
<%_ for (idx in id) { _%>
        this.<%= id[idx].attributeName %>=<%= id[idx].attributeName %>;
<%_ } _%>
    }

<%_ for (idx in id) {
    const fieldType = id[idx].type;
    const fieldName = id[idx].attributeName;
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
<%_ for (idx in id) { _%>
        <% if(idx==0){ %>return <% }else{ %>    && <% } %>Objects.equals(<%= id[idx].attributeName %>, businessBasicIndexId.<%= id[idx].attributeName %>)
<%_ } _%>
        ;
    }

    @Override
    public int hashCode() {
        int result = 17;
<%_ for (idx in id) { _%>
        result = 31 * result + <%= id[idx].attributeName %>.hashCode();
<%_ } _%>
        return result;
    }


}
