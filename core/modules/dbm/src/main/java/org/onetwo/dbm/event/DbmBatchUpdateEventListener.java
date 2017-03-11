package org.onetwo.dbm.event;

import org.onetwo.common.utils.LangUtils;
import org.onetwo.dbm.exception.DbmException;
import org.onetwo.dbm.mapping.JFishMappedEntry;

public class DbmBatchUpdateEventListener extends UpdateEventListener {

	@Override
	public void doEvent(DbmEvent event) {
		JFishMappedEntry entry = event.getEventSource().getMappedEntryManager().findEntry(event.getObject());
		if(entry==null){
			event.setUpdateCount(0);
			return ;
		}
		super.doEvent(event);
	}
	
	@Override
	protected void doUpdate(DbmUpdateEvent event, JFishMappedEntry entry){
		Object entity = event.getObject();
		if(!LangUtils.isMultiple(entity)){
			throw new DbmException("batch update's args must be a Collection or Array!");
		}
		int count = this.executeJdbcUpdate(event.getEventSource(), entry.makeUpdate(entity));
		event.setUpdateCount(count);
	}

}