package no.haagensoftware.kontize.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import no.haagensoftware.contentice.handler.ContenticeHandler;
import no.haagensoftware.kontize.db.LevelDbEnv;
import no.haagensoftware.kontize.models.AdminKey;
import no.haagensoftware.kontize.models.AdminKeyList;
import no.haagensoftware.kontize.models.AdminKeyObject;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by jhsmbp on 1/27/14.
 */
public class AdminKeysHandler extends ContenticeHandler {
    private static final Logger logger = Logger.getLogger(AdminKeysHandler.class.getName());

    public AdminKeysHandler() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        String jsonReturn = "";

        String adminDataTypeId = getParameter("adminKey");

        if (isGet(fullHttpRequest) && adminDataTypeId != null) {
            //Get single admin data type
        } else if (isGet(fullHttpRequest) && adminDataTypeId == null) {
             List<AdminKey> adminKeys = LevelDbEnv.getInstance().getUserDao().getKeys();

            AdminKeyList adminKeyList = new AdminKeyList();
            adminKeyList.setAdminKeys(adminKeys);

            jsonReturn = new Gson().toJson(adminKeyList);
        } else if (isPut(fullHttpRequest) && adminDataTypeId != null) {
            String messageContent = getHttpMessageContent(fullHttpRequest);
            AdminKeyObject adminKeyObject = new Gson().fromJson(messageContent, AdminKeyObject.class);
            adminKeyObject.getAdminKey().setId(adminDataTypeId);

            AdminKey adminKey = adminKeyObject.getAdminKey();

            logger.info(new Gson().toJson(adminKey));
            LevelDbEnv.getInstance().getUserDao().storeKey(adminKey.getId(), adminKey.getValue());

            jsonReturn = new Gson().toJson(adminKeyObject);
        } else if (isDelete(fullHttpRequest) && adminDataTypeId != null) {
            LevelDbEnv.getInstance().getUserDao().deleteKey(adminDataTypeId);
        }

        writeContentsToBuffer(channelHandlerContext, jsonReturn, "application/json; charset=UTF-8");
    }

}
