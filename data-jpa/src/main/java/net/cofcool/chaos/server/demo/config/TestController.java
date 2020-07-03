package net.cofcool.chaos.server.demo.config;

import javax.annotation.Resource;
import net.cofcool.chaos.server.common.core.ExecuteResult;
import net.cofcool.chaos.server.common.core.QueryResult;
import net.cofcool.chaos.server.common.core.SimplePage;
import net.cofcool.chaos.server.core.annotation.Api;
import net.cofcool.chaos.server.demo.data.jpa.repository.entity.Person;
import net.cofcool.chaos.server.demo.item.PersonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private PersonService<Person> personService;

    @PostMapping("/query")
    public QueryResult<Person, ?> query(@RequestBody SimplePage<Person> page) {
        return personService.query(page, page.getCondition());
    }

    @GetMapping("/detail")
    public ExecuteResult<Person> detail(String name) {
        return personService.queryByName(name);
    }


}
