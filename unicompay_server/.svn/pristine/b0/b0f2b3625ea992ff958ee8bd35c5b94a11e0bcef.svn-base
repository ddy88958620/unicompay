package com.chinaunicom.unipay.ws.persistence;

import com.chinaunicom.unipay.ws.utils.Tools;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * User: Frank
 * Date: 2014/11/27
 * Time: 15:32
 */
public class BindCard extends Model<BindCard> {

    private static final Logger logger = LoggerFactory.getLogger(BindCard.class);

    public static final BindCard dao = new BindCard();
    public static final String TABLE = "unipay_cardinfo";
    public static final String ID = "cardid";

    public List<BindCard> getByIdentityId(String identityId) {
        return find("select * from " + TABLE + " t where t.useraccount = ?", identityId);
    }

    public List<BindCard> getByCardno(String card_top,String card_last){
        return find("select * from " + TABLE + " t where t.card_top = ? and t.card_last = ?",card_top,card_last);
    }

    public void deleteByIdentityId(String identityId) {
        for(BindCard bindCard : getByIdentityId(identityId)) {
            bindCard.delete();
        }
    }

    public void setDefault(final String identityid, final String bindid) {

        Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                final long start = System.currentTimeMillis();
                Db.update("update " + TABLE + " set isdefault = 0 where useraccount = ? and cardid != ?", identityid, bindid);
                Db.update("update " + TABLE + " set isdefault = 1 where cardid = ?", bindid);
                logger.debug("绑卡默认卡设置[" + getCardid() + "]成功" + "|用时=" + (System.currentTimeMillis() - start) + "ms");
                return true;
            }
        });
    }

    @Override
    public boolean update() {
        final long start = System.currentTimeMillis();
        final boolean result = super.update();

        logger.debug("绑卡更新[" + getCardid() + "]" + (result ? "成功" : "失败") + "|用时=" + (System.currentTimeMillis() - start) + "ms");
        return result;
    }


    @Override
    public boolean save() {
        final long start = System.currentTimeMillis();
        final boolean result = super.save();

        logger.debug("绑卡创建[" + getCardid() + "]" + (result ? "成功" : "失败") + "|用时=" + (System.currentTimeMillis() - start) + "ms");
        return result;
    }

    public String getCardname() {
        return get("card_name");
    }

    public BindCard setCardname(String cardname) {
        set("card_name", cardname);
        return this;
    }

    public String getCardid() {
        return get("cardid");
    }

    public BindCard setCardid(String cardid) {
        set("cardid", cardid);
        return this;
    }

    public String getCardno() {
        return get("cardno");
    }

    public BindCard setCardno(String cardno) {
        set("cardno", cardno);
        return this;
    }

    public String getCardtop() {
        return get("card_top");
    }

    public BindCard setCardtop(String cardtop) {
        set("card_top", cardtop);
        return this;
    }

    public String getCardlast() {
        return get("card_last");
    }

    public BindCard setCardlast(String cardlast) {
        set("card_last", cardlast);
        return this;
    }

    public int getIsdefault() {
        return getBigDecimal("isdefault").intValue();
    }

    public BindCard setIsDefault(int isdefault) {
        set("isdefault", new BigDecimal(isdefault));
        return this;
    }

    public BindCard setIdentityId(String identityId) {
        set("useraccount", identityId);
        return this;
    }

    public String getIdentityId(){
        return get("useraccount");
    }

    public BindCard setBindvalidthru(int bindvalidthru) {
        set("bindvalidthru", Tools.getCommonTime(bindvalidthru));
        return this;
    }

}
