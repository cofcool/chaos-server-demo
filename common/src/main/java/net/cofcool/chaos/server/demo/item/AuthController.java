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

package net.cofcool.chaos.server.demo.item;

import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.core.SimplePage;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.core.annotation.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author CofCool
 */
@Api
@RestController
@RequestMapping(value = "/auth", method = {RequestMethod.GET, RequestMethod.POST})
public class AuthController {

    private PersonService<Person> personService;

    @Autowired
    public void setPersonService(PersonService<Person> personService) {
        this.personService = personService;
    }

    @RequestMapping("/user")
    public Message<User> test(User user) {
        return ConfigurationSupport.getConfiguration().getMessageWithKey(
            ExceptionCodeDescriptor.SERVER_OK,
            ExceptionCodeDescriptor.SERVER_OK_DESC,
            user
        );
    }

    @RequestMapping("/unauth")
    public Message unauth(String ex) {
        return ConfigurationSupport.getConfiguration().getMessage(
            ExceptionCodeDescriptor.AUTH_ERROR,
            true,
            ex,
            false,
            null
        );
    }


    @RequestMapping("/unlogin")
    public Message unlogin(String ex) {
        return ConfigurationSupport.getConfiguration().getMessage(
            ExceptionCodeDescriptor.NO_LOGIN,
            true,
            ex,
            false,
            null
        );
    }

    @RequestMapping("/query")
    public Message query(@RequestBody SimplePage<Person> page) {
        return personService.query(page, page.getCondition()).result();
    }
}
