/*
 * Copyright (c) 2010-2012. Axon Framework
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

package org.axonframework.samples.trader.webui.admin;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.samples.trader.api.orders.OrderBookId;
import org.axonframework.samples.trader.api.portfolio.PortfolioId;
import org.axonframework.samples.trader.api.portfolio.cash.DepositCashCommand;
import org.axonframework.samples.trader.api.portfolio.stock.AddItemsToPortfolioCommand;
import org.axonframework.samples.trader.query.orderbook.OrderBookView;
import org.axonframework.samples.trader.query.orderbook.OrderBookViewRepository;
import org.axonframework.samples.trader.query.portfolio.PortfolioView;
import org.axonframework.samples.trader.query.portfolio.PortfolioViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private CommandBus commandBus;
    private PortfolioViewRepository portfolioViewRepository;
    private OrderBookViewRepository orderBookViewRepository;

    @RequestMapping(value = "/portfolio")
    public String show(Model model) {
        Iterable<PortfolioView> portfolios = portfolioViewRepository.findAll();
        model.addAttribute("portfolios", portfolios);

        return "admin/portfolio/list";
    }

    @RequestMapping(value = "/portfolio/{identifier}")
    public String showPortfolio(@PathVariable("identifier") String portfolioIdentifier,
                                Model model) {
        PortfolioView portfolio = portfolioViewRepository.findOne(portfolioIdentifier);
        model.addAttribute("portfolio", portfolio);

        Iterable<OrderBookView> orderBooks = orderBookViewRepository.findAll();
        model.addAttribute("orderbooks", orderBooks);

        return "admin/portfolio/detail";
    }

    @RequestMapping(value = "/portfolio/{identifier}/money")
    public String addMoney(@PathVariable("identifier") String portfolioIdentifier,
                           @RequestParam("amount") long amountOfMoney
    ) {
        DepositCashCommand command =
                new DepositCashCommand(new PortfolioId(portfolioIdentifier), amountOfMoney);
        commandBus.dispatch(new GenericCommandMessage<>(command));
        return "redirect:/admin/portfolio/{identifier}";
    }

    @RequestMapping(value = "/portfolio/{identifier}/item")
    public String addItem(@PathVariable("identifier") String portfolioIdentifier,
                          @RequestParam("orderbook") String orderBookIdentifier,
                          @RequestParam("amount") long amount
    ) {
        AddItemsToPortfolioCommand command = new AddItemsToPortfolioCommand(new PortfolioId(
                portfolioIdentifier),
                                                                            new OrderBookId(
                                                                                    orderBookIdentifier),
                                                                            amount);
        commandBus.dispatch(new GenericCommandMessage<>(command));
        return "redirect:/admin/portfolio/{identifier}";
    }

    /* Setters */
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setCommandBus(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setOrderBookViewRepository(OrderBookViewRepository orderBookViewRepository) {
        this.orderBookViewRepository = orderBookViewRepository;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setPortfolioViewRepository(PortfolioViewRepository portfolioViewRepository) {
        this.portfolioViewRepository = portfolioViewRepository;
    }
}
