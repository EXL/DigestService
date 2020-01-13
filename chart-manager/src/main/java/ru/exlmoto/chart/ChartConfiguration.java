package ru.exlmoto.chart;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties("chart")
public class ChartConfiguration {

}
