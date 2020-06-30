/*
 * Copyright 2019 cofcool
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.cofcool.chaos.server.demo.api;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import net.cofcool.chaos.server.common.security.AbstractLogin.DefaultLogin;
import net.cofcool.chaos.server.common.security.Device;
import net.cofcool.chaos.server.common.security.exception.CaptchaErrorException;
import net.cofcool.chaos.server.core.support.SimpleExceptionCodeDescriptor;


public class Login extends DefaultLogin {

    private static final long serialVersionUID = -3868201780589666741L;

    @NotNull
    private String username;

    @NotNull
    private String password;

    private String captcha;

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    protected Device checkRequestAgent(HttpServletRequest servletRequest) {
        String platform = servletRequest.getParameter("platform");
        if (net.cofcool.chaos.server.demo.api.Device.ANDROID.identifier().equals(platform)) {
            return net.cofcool.chaos.server.demo.api.Device.ANDROID;
        }

        return super.checkRequestAgent(servletRequest);
    }

    @Override
    protected void checkCaptcha(HttpServletRequest servletRequest,
        net.cofcool.chaos.server.common.security.Device device) {
        // just test
        if (captcha != null && captcha.equalsIgnoreCase("1234")) {
            throw new CaptchaErrorException("captcha error", SimpleExceptionCodeDescriptor.CAPTCHA_ERROR_VAL);
        }
    }

}
