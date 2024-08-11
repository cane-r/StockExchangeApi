package com.inghubs.stockexchangeapi;

import com.inghubs.stockexchangeapi.models.entity.Stock;
import com.inghubs.stockexchangeapi.models.entity.StockExchange;
import com.inghubs.stockexchangeapi.models.event.StockAddedToExchangeEvent;
import com.inghubs.stockexchangeapi.service.StockExchangeService;
import com.inghubs.stockexchangeapi.service.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
class StockExchangeApiApplicationTests {
	@Autowired
	private ApplicationContext context;

	@Autowired
	private StockExchangeService service;

	@Autowired
	private StockService stockService;

	@MockBean
	private TestEventListener consumer;


	@Test
	void contextLoads() {
		final Long bist30 = service.countStocksByExchangeName("BIST30");
		assertThat(bist30).isNotNull();
	}

	@Test
	@Transactional
	void testIsLiveAsyncCallFunctionality() {
		StockExchange exchange = service.getStockExchangeByName("BIST30");

		Stock stock = stockService.getStockById(9L);

		assertThat(stock).isNotNull();

		service.addStockToStockExchange(exchange.getName(),stock.getId());

//		Awaitility.setDefaultPollInterval(10, TimeUnit.MILLISECONDS);
//		Awaitility.setDefaultPollDelay(Duration.ZERO);
//		Awaitility.setDefaultTimeout(Duration.ofMinutes(1));
//		await()
//				.atLeast(Duration.ofSeconds(20))
//				.atMost(Duration.ofMinutes(1))
//				.with()
//				.pollInterval(Duration.ofSeconds(1))
//				.untilAsserted(() -> assertThat(service.getStockExchangeByName("BIST30").getLiveInMarket()).isTrue()
//				);
		//publisher.publishEvent(new StockAddedToExchangeEvent(this,stock,exchange.getName()));
		//verify(consumer).consumeEvent(any(StockAddedToExchangeEvent.class));
		verify(consumer).consumeEvent(any(StockAddedToExchangeEvent.class));
	}
	@TestComponent
	private static class TestEventListener {
		@EventListener
		public void consumeEvent(StockAddedToExchangeEvent testEvent) {
		}
	}
}
