package com.ermao.aicode.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 删除请求
 * @author 21195
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;
}