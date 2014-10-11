/*
 *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.wso2.carbon.rssmanager.core.manager.impl.h2;

import org.wso2.carbon.rssmanager.core.dao.util.EntityManager;
import org.wso2.carbon.rssmanager.core.exception.RSSManagerException;

import javax.persistence.Query;

public class H2JPAResolver {
	
	private EntityManager entityManager;
	
	
	
	public H2JPAResolver(EntityManager entityManager) {
	    super();
	    this.entityManager = entityManager;
    }



	public void initializeSequences() throws RSSManagerException{
		entityManager.beginTransaction();
		executeNativeStatment("insert into ENVIRONMENT_SEQUENCE_TABLE  (SEQ_NAME,SEQ_COUNT) select 'EMP_SEQ',0 from dual  where not exists (select SEQ_NAME from ENVIRONMENT_SEQUENCE_TABLE) ");
		executeNativeStatment("insert into DATABASE_SEQUENCE_TABLE (SEQ_NAME,SEQ_COUNT) select 'EMP_SEQ',0 from dual  where not exists (select SEQ_NAME from DATABASE_SEQUENCE_TABLE) ");
		executeNativeStatment("insert into SERVER_INSTANCE_SEQUENCE_TABLE (SEQ_NAME,SEQ_COUNT) select 'EMP_SEQ',0 from dual  where not exists (select SEQ_NAME from SERVER_INSTANCE_SEQUENCE_TABLE) ");
		executeNativeStatment("insert into DATABASE_USER_SEQUENCE_TABLE (SEQ_NAME,SEQ_COUNT) select 'EMP_SEQ',0 from dual  where not exists (select SEQ_NAME from DATABASE_USER_SEQUENCE_TABLE) ");
		executeNativeStatment("insert into PRIVILEGE_TEMPLATE_SEQUENCE_TABLE (SEQ_NAME,SEQ_COUNT) select 'EMP_SEQ',0 from dual  where not exists (select SEQ_NAME from PRIVILEGE_TEMPLATE_SEQUENCE_TABLE) ");
		executeNativeStatment("insert into DB_PRIVILEGE_TEMPLATE_ENTRY_SEQUENCE_TABLE (SEQ_NAME,SEQ_COUNT) select 'EMP_SEQ',0 from dual  where not exists (select SEQ_NAME from DB_PRIVILEGE_TEMPLATE_ENTRY_SEQUENCE_TABLE) ");
		executeNativeStatment("insert into USER_DATABASE_PRIVILEGE_SEQUENCE_TABLE (SEQ_NAME,SEQ_COUNT) select 'EMP_SEQ',0 from dual  where not exists (select SEQ_NAME from USER_DATABASE_PRIVILEGE_SEQUENCE_TABLE) ");
		executeNativeStatment("insert into USER_DATABASE_ENTRY_SEQUENCE_TABLE (SEQ_NAME,SEQ_COUNT) select 'EMP_SEQ',0 from dual  where not exists (select SEQ_NAME from USER_DATABASE_ENTRY_SEQUENCE_TABLE) ;");

		entityManager.endJPATransaction();
	}
	
	private void executeNativeStatment(String sql){
		Query query = entityManager.getJpaUtil().getJPAEntityManager().createNativeQuery(sql);
		query.executeUpdate();
	}

}
