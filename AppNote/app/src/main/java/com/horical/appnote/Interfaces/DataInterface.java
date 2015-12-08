package com.horical.appnote.Interfaces;

import com.horical.appnote.DTO.BaseDTO;

import java.util.ArrayList;

/**
 * Created by Phuong on 14/09/2015.
 */
public interface DataInterface<T extends BaseDTO> {
    int compare(T dto);
}
