package cms.cmsconst;

import com.boco.protocolBody.ReturnCode;

import java.io.Serializable;

/**
 * 返回结果
 *
 */
public class ReturnState implements Serializable {

    private static final long serialVersionUID = 7795348575057274545L;
    /**
     *返回值，6 位 10 进制数，000000 表示成功，其他表示失败
     */
    private ReturnCode returnCode;
    /**
     * 返回的内容描述，成功的命令不需要填
     */
    private String returnMessage;


    public ReturnCode getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(ReturnCode returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }
}
