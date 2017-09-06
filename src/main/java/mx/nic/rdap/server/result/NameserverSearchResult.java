package mx.nic.rdap.server.result;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.catalog.RemarkType;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.db.struct.SearchResultStruct;
import mx.nic.rdap.server.renderer.json.NameserverJsonWriter;

/**
 * A result from a Nameserver search request
 */
public class NameserverSearchResult extends RdapResult {

	private List<Nameserver> nameservers;
	// The max number of results allowed for the user
	private Integer maxNumberOfResultsForUser;
	// Indicate is the search has more results than the answered to the user
	private Boolean resultSetWasLimitedByUserConfiguration;

	public NameserverSearchResult(String header, String contextPath, SearchResultStruct<Nameserver> result,
			String userName) {
		notices = new ArrayList<Remark>();
		this.nameservers = new ArrayList<Nameserver>();
		this.userInfo = new UserInfo(userName);
		this.setMaxNumberOfResultsForUser(result.getSearchResultsLimitForUser());
		this.resultSetWasLimitedByUserConfiguration = result.getResultSetWasLimitedByUserConfiguration();
		for (Nameserver nameserver : result.getResults()) {
			NameserverResult.addSelfLinks(header, contextPath, nameserver);
			this.nameservers.add(nameserver);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		for (Nameserver nameserver : nameservers) {
			arrayBuilder.add(NameserverJsonWriter.getJson(nameserver, userInfo.isUserAuthenticated(),
					userInfo.isOwner(nameserver)));
		}
		builder.add("nameserverSearchResults", arrayBuilder.build());
		return builder.build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#fillNotices()
	 */
	@Override
	public void fillNotices() {
		// At the moment, we only add the privacy notice
		if (nameservers.size() < maxNumberOfResultsForUser && resultSetWasLimitedByUserConfiguration) {
			notices.add(new Remark(RemarkType.RESULT_SET_UNEXPLAINABLE));
		} else if (nameservers.size() == maxNumberOfResultsForUser) {
			notices.add(new Remark(RemarkType.RESULT_SET_AUTHORIZATION));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#validateResponse()
	 */
	@Override
	public void validateResponse() {
		// Nothing to validate
	}

	public Integer getMaxNumberOfResultsForUser() {
		return maxNumberOfResultsForUser;
	}

	public void setMaxNumberOfResultsForUser(Integer maxNumberOfResultsForUser) {
		this.maxNumberOfResultsForUser = maxNumberOfResultsForUser;
	}

	public Boolean getResultSetWasLimitedByUserConfiguration() {
		return resultSetWasLimitedByUserConfiguration;
	}

	public void setResultSetWasLimitedByUserConfiguration(Boolean resultSetWasLimitedByUserConfiguration) {
		this.resultSetWasLimitedByUserConfiguration = resultSetWasLimitedByUserConfiguration;
	}

}
