package com.disk.files.exception;

import com.disk.base.exception.ErrorCode;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 * @date 2024/10/19
 */
public enum FilesErrorCode implements ErrorCode {

    FILE_NOT_EXIT("FILE_NOT_EXIT", "文件不存在"),

    FILE_NOT_CUR_USER("FILE_NOT_CUR_USER", "非当前用户文件"),
    FILE_NEW_NAME_EQUALS("FILE_NEW_NAME_EQUALS", "请指定新文件名"),
    FILE_NEW_NAME_EXIST("FILE_NEW_NAME_EXIST", "文件名已存在"),
    FILE_DELETE_ERROR("FILE_DELETE_ERROR", "文件删除失败"),
    FILE_RENAME_ERROR("FILE_RENAME_ERROR", "文件重命名失败"),
    FILE_IDENTIFIER_NOT_EXIST("FILE_IDENTIFIER_NOT_EXIST", "文件唯一标识符不存在"),
    FILE_NO_AUTH("FILE_NO_AUTH", "无权限操作该文件"),
    FOLDER_NOT_DOWNLOAD("FOLDER_NOT_DOWNLOAD", "文件夹暂不支持下载"),
    TARGET_FOLDER_TYPE_ERROR("TARGET_FOLDER_TYPE_ERROR", "目标文件不是一个文件夹"),
    FOLDER_INVALID("FOLDER_INVALID", "目标文件夹ID不能是选中文件列表的文件夹ID或其子文件夹ID"),

    ;
    private final String code;

    private final String message;


    FilesErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
