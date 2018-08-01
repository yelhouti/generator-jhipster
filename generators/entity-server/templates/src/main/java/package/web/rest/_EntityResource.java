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
package <%=packageName%>.web.rest;

import com.codahale.metrics.annotation.Timed;
<%_ if (dto !== 'mapstruct' || service === 'no') { _%>
import <%=packageName%>.domain.<%= entityClass %>;
<%_ } _%>
<%_ if(primaryKeyCount > 1){ _%>
import <%=packageName%>.domain.<%=entityClass%>Id;
<%_ } _%>
<%_ if (service !== 'no') { _%>
import <%=packageName%>.service.<%= entityClass %>Service;<% } else { %>
import <%=packageName%>.repository.<%= entityClass %>Repository;<% if (searchEngine === 'elasticsearch') { %>
import <%=packageName%>.repository.search.<%= entityClass %>SearchRepository;<% }} %>
import <%=packageName%>.web.rest.errors.BadRequestAlertException;
import <%=packageName%>.web.rest.util.HeaderUtil;<% if (pagination !== 'no') { %>
import <%=packageName%>.web.rest.util.PaginationUtil;<% } %>
<%_ if (dto === 'mapstruct') { _%>
import <%=packageName%>.service.dto.<%= entityClass %>DTO;
<%_ if (service === 'no') { _%>
import <%=packageName%>.service.mapper.<%= entityClass %>Mapper;
<%_ } } _%>
<%_ if (jpaMetamodelFiltering) {  _%>
import <%=packageName%>.service.dto.<%= entityClass %>Criteria;
import <%=packageName%>.service.<%= entityClass %>QueryService;
<%_ } _%>
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
<%_ if (pagination !== 'no') { _%>
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
<%_ } _%>
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
<% if (validation) { %>
import javax.validation.Valid;<% } %>
import java.net.URI;
import java.net.URISyntaxException;
<%_ const viaService = service !== 'no';
    if (pagination === 'no' && dto === 'mapstruct' && !viaService && fieldsContainNoOwnerOneToOne === true) { _%>
import java.util.LinkedList;<% } %>
import java.util.List;
import java.util.Optional;<% if (databaseType === 'cassandra') { %>
import java.util.UUID;<% } %><% if (!viaService && (searchEngine === 'elasticsearch' || fieldsContainNoOwnerOneToOne === true)) { %>
import java.util.stream.Collectors;<% } %><% if (searchEngine === 'elasticsearch' || fieldsContainNoOwnerOneToOne === true) { %>
import java.util.stream.StreamSupport;<% } %><% if (searchEngine === 'elasticsearch') { %>

import static org.elasticsearch.index.query.QueryBuilders.*;<% } %>
<%_
idFields = []
for (idx in relationships){
    if(relationships[idx].primaryKey){
        let field={};
        //TODO set field from relashionship
        field.fieldType = "Long"
        field.fieldName = relationships[idx].relationshipName+"Id"
        idFields.push(field);
    }
}
for (idx in fields){
    if(fields[idx].primaryKey){
        idFields.push(fields[idx]);
    }
}
_%>
<%_
// attribute types to id Field (should be put in json?)
for (idx in idFields) {
    if (idFields[idx].fieldType.toLowerCase() === 'boolean') {
        idFields[idx].getter="is";
    } else {
        idFields[idx].getter="get";
    }
    idFields[idx].getter+=idFields[idx].fieldName.charAt(0).toUpperCase() + idFields[idx].fieldName.slice(1)+ "()";
}
let idPath="";
let pathVariable="";
let idParams="";
if(primaryKeyCount > 1){
    for(idx in idFields){
        idPath+=(((idx==0)?"/":",")+"{"+idFields[idx].fieldName+"}");
        pathVariable+=('@PathVariable '+idFields[idx].fieldType+' '+idFields[idx].fieldName+', ');
        idParams+=(idFields[idx].fieldName+", ");
    }
    pathVariable=pathVariable.slice(0,-2)
    idParams=idParams.slice(0,-2)
} else {
    idPath="/{id}";
    pathVariable="@PathVariable "+pkType+" id";
}
_%>

/**
 * REST controller for managing <%= entityClass %>.
 */
@RestController
@RequestMapping("/api")
public class <%= entityClass %>Resource {

    private final Logger log = LoggerFactory.getLogger(<%= entityClass %>Resource.class);

    private static final String ENTITY_NAME = "<%= entityInstance %>";
    <%_
    const idInstanceName = entityInstance + "Id";
    const instanceType = (dto === 'mapstruct') ? entityClass + 'DTO' : entityClass;
    const instanceName = (dto === 'mapstruct') ? entityInstance + 'DTO' : entityInstance;
    _%><%- include('../../common/inject_template', {viaService: viaService, constructorName: entityClass + 'Resource', queryService: jpaMetamodelFiltering}); -%>

    /**
     * POST  /<%= entityApiUrl %> : Create a new <%= entityInstance %>.
     *
     * @param <%= instanceName %> the <%= instanceName %> to create
     * @return the ResponseEntity with status 201 (Created) and with body the new <%= instanceName %>, or with status 400 (Bad Request) if the <%= entityInstance %> has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/<%= entityApiUrl %>")
    @Timed
    public ResponseEntity<<%= instanceType %>> create<%= entityClass %>(<% if (validation) { %>@Valid <% } %>@RequestBody <%= instanceType %> <%= instanceName %>) throws URISyntaxException {
        log.debug("REST request to save <%= entityClass %> : {}", <%= instanceName %>);
        <%_ if ( typeof id === 'undefined' && primaryKeyCount == 0) { _%>
        if (<%= instanceName %>.getId() != null) {
            throw new BadRequestAlertException("A new <%= entityInstance %> cannot already have an ID", ENTITY_NAME, "idexists");
        }<%_ } _%><%- include('../../common/save_template', {viaService: viaService, returnDirectly: false}); -%>
        <%_
        if ( typeof id === 'undefined' && primaryKeyCount == 0) {
            path='"/"+result.getId().toString()'
        } else {
            path='';
            for (idx in idFields) {
                path+='"/"+result.'+idFields[idx].getter+"+";
            }
            path=path.slice(0, -1);
        }
        _%>
        return ResponseEntity.created(new URI("/api/<%= entityApiUrl %>" + <%- path -%>))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, <%- path -%>))
            .body(result);
    }

    /**
     * PUT  /<%= entityApiUrl %> : Updates an existing <%= entityInstance %>.
     *
     * @param <%= instanceName %> the <%= instanceName %> to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated <%= instanceName %>,
     * or with status 400 (Bad Request) if the <%= instanceName %> is not valid,
     * or with status 500 (Internal Server Error) if the <%= instanceName %> couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/<%= entityApiUrl %>")
    @Timed
    public ResponseEntity<<%= instanceType %>> update<%= entityClass %>(<% if (validation) { %>@Valid <% } %>@RequestBody <%= instanceType %> <%= instanceName %>) throws URISyntaxException {
        log.debug("REST request to update <%= entityClass %> : {}", <%= instanceName %>);
        <%_ if ( typeof id === 'undefined' && primaryKeyCount == 0) { _%>
        if (<%= instanceName %>.getId() == null) {
            return create<%= entityClass %>(<%= instanceName %>);
        }<%_ } _%><%- include('../../common/save_template', {viaService: viaService, returnDirectly: false}); -%>
                <%_
        if ( typeof id === 'undefined' && primaryKeyCount == 0) {
            path='"/"+'+instanceName+'.getId().toString()'
        } else {
            path='';
            for (idx in idFields) {
                path+='"/"+'+instanceName+'.'+idFields[idx].getter+"+";
            }
            path=path.slice(0, -1);
        }
        _%>
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, <%- path -%>))
            .body(result);
    }

    /**
     * GET  /<%= entityApiUrl %> : get all the <%= entityInstancePlural %>.
     *<% if (pagination !== 'no') { %>
     * @param pageable the pagination information<% } if (jpaMetamodelFiltering) { %>
     * @param criteria the criterias which the requested entities should match<% } else if (fieldsContainNoOwnerOneToOne) { %>
     * @param filter the filter of the request<% } %>
     * @return the ResponseEntity with status 200 (OK) and the list of <%= entityInstancePlural %> in body
     */
    @GetMapping("/<%= entityApiUrl %>")
    @Timed<%- include('../../common/get_all_template', {viaService: viaService}); -%>

    /**
     * GET  /<%= entityApiUrl %>/:id : get the "id" <%= entityInstance %>.
     *
<%_ if( typeof id === 'undefined' && primaryKeyCount == 0){ _%>
     * @param id the id of the <%= instanceName %> to retrieve
<%_ } else {_%>
    <%_ for (idx in idFields) { _%>
     * @param <%= idFields[idx].fieldName %> the <%= idFields[idx].fieldName %> of the <%= instanceName %> to retrieve
    <%_ } _%>
<%_ } _%>
     * @return the ResponseEntity with status 200 (OK) and with body the <%= instanceName %>, or with status 404 (Not Found)
     */
    @GetMapping("/<%= entityApiUrl %><%= idPath %>")
    @Timed
    public ResponseEntity<<%= instanceType %>> get<%= entityClass %>(<%= pathVariable %>) {
<%_ if(primaryKeyCount > 1){ _%>
        <%= entityClass %>Id id = new <%= entityClass %>Id(<%= idParams %>);
<%_ } _%>
        log.debug("REST request to get <%= entityClass %> : {}", id);<%- include('../../common/get_template', {viaService: viaService, returnDirectly:false}); -%>
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(<%= instanceName %>));
    }

    /**
     * DELETE  /<%= entityApiUrl %>/:id : delete the "id" <%= entityInstance %>.
     *
<%_ if( typeof id === 'undefined' && primaryKeyCount == 0){ _%>
     * @param id the id of the <%= instanceName %> to retrieve
<%_ } else {_%>
    <%_ for (idx in idFields) { _%>
     * @param <%= idFields[idx].fieldName %> the <%= idFields[idx].fieldName %> of the <%= instanceName %> to retrieve
    <%_ } _%>
<%_ } _%>
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/<%= entityApiUrl %><%= idPath %>")
    @Timed
    public ResponseEntity<Void> delete<%= entityClass %>(<%= pathVariable %>) {
<%_ if(primaryKeyCount > 1){ _%>
        <%= entityClass %>Id id = new <%= entityClass %>Id(<%= idParams %>);
<%_ } _%>
        log.debug("REST request to delete <%= entityClass %> : {}", id);<%- include('../../common/delete_template', {viaService: viaService}); -%>
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id<% if (pkType !== 'String') { %>.toString()<% } %>)).build();
    }<% if (searchEngine === 'elasticsearch') { %>

    /**
     * SEARCH  /_search/<%= entityApiUrl %>?query=:query : search for the <%= entityInstance %> corresponding
     * to the query.
     *
     * @param query the query of the <%= entityInstance %> search<% if (pagination !== 'no') { %>
     * @param pageable the pagination information<% } %>
     * @return the result of the search
     */
    @GetMapping("/_search/<%= entityApiUrl %>")
    @Timed<%- include('../../common/search_template', {viaService: viaService}); -%><% } %>
}
