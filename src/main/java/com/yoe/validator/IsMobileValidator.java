package com.yoe.validator;

import com.yoe.utils.ValidatorUtil;
import org.thymeleaf.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {

    private boolean required = false;

    @Override
    public void initialize(IsMobile isMobile) {
        required = isMobile.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        //如果该参数是必须的，走该分支
        if(required){
            return ValidatorUtil.isMobile(s);
        }else{
            //不是必须的走这个分支

            //如果参数为空，直接返回true
            if(StringUtils.isEmpty(s)){
                return true;
            }else{
                //参数不为空，则需要判断
                return ValidatorUtil.isMobile(s);
            }
        }
    }
}
