package com.disk.api.files.request.condition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserRootFolderQueryCondition implements UserFileQueryCondition {

    private static final long serialVersionUID = -6080902404166212851L;

    /**
     * 用户ID
     */
    private Long userId;
}
