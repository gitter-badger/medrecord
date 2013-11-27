/**
 * This file is part of MEDrecord.
 * This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @copyright Copyright (c) 2013 MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.zorggemak.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@SuppressWarnings({"unchecked", "SpellCheckingInspection"})
@Controller
@RequestMapping("/")
public class HomeController {
    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/home")
    public String home() {
        return "index";
    }

    // using : since using a / makes spring-mvc want to route to
    // an action on a controller....or something like that
    @RequestMapping(value = "/{section:[a-zA-Z]+}:{pagename:[a-zA-Z]+}.page")
    public String jspPage(@PathVariable("section") String section, @PathVariable("pagename") String pagename) {
        return String.format("%s/%s", section, pagename);
    }

    @RequestMapping(value = "/{pagename:[a-zA-Z]+}.page")
    public String jspPage(@PathVariable("pagename") String pagename) {
        return pagename;
    }
}
