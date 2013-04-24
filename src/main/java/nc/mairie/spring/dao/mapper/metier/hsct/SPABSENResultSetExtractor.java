package nc.mairie.spring.dao.mapper.metier.hsct;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.hsct.SPABSENDao;
import nc.mairie.spring.domain.metier.hsct.SPABSEN;

import org.springframework.jdbc.core.ResultSetExtractor;

public class SPABSENResultSetExtractor implements ResultSetExtractor<Object> {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		SPABSEN absen = new SPABSEN();
		absen.setNoMatr(rs.getInt(SPABSENDao.CHAMP_NOMATR));
		absen.setType(rs.getString(SPABSENDao.CHAMP_TYPE3));
		absen.setDatDeb(rs.getInt(SPABSENDao.CHAMP_DATDEB));
		absen.setDatFin(rs.getInt(SPABSENDao.CHAMP_DATFIN));
		absen.setNbJour(rs.getInt(SPABSENDao.CHAMP_NBJOUR));
		absen.setTotPri(rs.getInt(SPABSENDao.CHAMP_TOTPRI));
		absen.setNbjCds(rs.getInt(SPABSENDao.CHAMP_NBJCDS));
		absen.setNbjCps(rs.getInt(SPABSENDao.CHAMP_NBJCPS));
		absen.setRapps(rs.getInt(SPABSENDao.CHAMP_RAPPS));
		absen.setRapds(rs.getInt(SPABSENDao.CHAMP_RAPDS));
		absen.setDatEss(rs.getInt(SPABSENDao.CHAMP_DATESS));
		return absen;
	}
}
