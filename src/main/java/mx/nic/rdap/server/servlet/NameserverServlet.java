package mx.nic.rdap.server.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;

import mx.nic.rdap.core.db.DomainLabel;
import mx.nic.rdap.core.db.DomainLabelException;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.exception.http.BadRequestException;
import mx.nic.rdap.db.exception.http.HttpException;
import mx.nic.rdap.db.exception.http.NotImplementedException;
import mx.nic.rdap.db.service.DataAccessService;
import mx.nic.rdap.db.spi.NameserverDAO;
import mx.nic.rdap.server.result.NameserverResult;
import mx.nic.rdap.server.result.RdapResult;
import mx.nic.rdap.server.util.Util;

@WebServlet(name = "nameserver", urlPatterns = { "/nameserver/*" })
public class NameserverServlet extends DataAccessServlet<NameserverDAO> {

	private static final long serialVersionUID = 1L;

	/**
	 * Constant value to set the maximum params expected in the URI, this servlet
	 * expects: nameserver
	 */
	private static final int MAX_PARAMS_EXPECTED = 1;

	@Override
	protected NameserverDAO initAccessDAO() throws RdapDataAccessException {
		return DataAccessService.getNameserverDAO();
	}

	@Override
	protected String getServedObjectName() {
		return "nameservers";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapServlet#doRdapGet(javax.servlet.http.
	 * HttpServletRequest)
	 */
	@Override
	protected RdapResult doRdapDaGet(HttpServletRequest httpRequest, NameserverDAO dao)
			throws HttpException, RdapDataAccessException {
		NameserverRequest request = new NameserverRequest(Util.getRequestParams(httpRequest, MAX_PARAMS_EXPECTED)[0]);

		DomainLabel label;
		try {
			label = new DomainLabel(request.getName());
		} catch (DomainLabelException e) {
			throw new BadRequestException(e);
		}

		Nameserver nameserver = dao.getByName(label);
		if (nameserver == null) {
			return null;
		}

		int nameserverCount = 0;
		boolean isNSSharingConformance = dao.isNameserverSharingNameConformance();
		if (isNSSharingConformance) {
			try {
				nameserverCount = dao.getNameserverCount(label);
			} catch (NotImplementedException e) {
				isNSSharingConformance = false;
			}
		}

		return new NameserverResult(Util.getServerUrl(httpRequest), httpRequest.getContextPath(), nameserver,
				Util.getUsername(SecurityUtils.getSubject()), nameserverCount, isNSSharingConformance);
	}

	private class NameserverRequest {

		private String name;

		public NameserverRequest(String name) {
			super();
			if (name.endsWith(".")) {
				name = name.substring(0, name.length() - 1);
			}
			this.name = name;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

	}

}
