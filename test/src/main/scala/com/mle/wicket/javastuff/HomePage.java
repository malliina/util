/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mle.wicket.javastuff;

import com.mle.wicket.markup.Home;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.atmosphere.Subscribe;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.Date;

public class HomePage extends WebPage {
    private static final long serialVersionUID = 1L;

    private Component timeLabel;
    private Component messageLabel;
    private TextField<String> input;

    public HomePage(final PageParameters parameters) {
        super(parameters);

        add(timeLabel = new Label("time", Model.of("start")).setOutputMarkupId(true));
        add(messageLabel = new Label("message", Model.of("-")).setOutputMarkupId(true));

        Form<Void> form = new Form<Void>("form");
        add(form);
        form.add(input = new TextField<String>("input", Model.of("")));
        form.add(new AjaxSubmitLink("send", form) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                AtmosphereApplication.get().getEventBus().post("Ohoi");
//                EventBus.get().post("oi: "+input.getModelObject());
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        });

        add(new Link("home") {
            @Override
            public void onClick() {
                setResponsePage(Home.class);
            }
        });
        setVersioned(false);
    }

    @Subscribe
    public void updateTime(AjaxRequestTarget target, Date event) {
        timeLabel.setDefaultModelObject(event.toString());
        target.add(timeLabel);
    }

    @Subscribe
    public void receiveMessage(AjaxRequestTarget target, String message) {
        messageLabel.setDefaultModelObject(message);
        target.add(messageLabel);
    }
}
