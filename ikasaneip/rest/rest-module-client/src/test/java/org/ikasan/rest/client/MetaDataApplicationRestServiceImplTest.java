package org.ikasan.rest.client;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.ikasan.rest.client.dto.ModuleDto;
import org.ikasan.spec.metadata.FlowMetaData;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

public class MetaDataApplicationRestServiceImplTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.options().dynamicPort());

    private MetaDataApplicationRestServiceImpl uut;

    private String contexBaseUrl;

    @Before
    public void setup()
    {
        contexBaseUrl = "http://localhost:" + wireMockRule.port();
        Environment environment = new StandardEnvironment();
        uut = new MetaDataApplicationRestServiceImpl(environment);

    }

    @Test
    public void getFlowMetaData()
    {
        stubFor(get(urlEqualTo("/rest/metadata/flow/moduleName/flowName"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("{\n" +
                    "      \"name\": \"Wriggle Request Inbound Flow\",\n" +
                    "      \"consumer\": {\n" +
                    "        \"componentName\": \"JMS Consumer\",\n" +
                    "        \"description\": null,\n" +
                    "        \"componentType\": \"org.ikasan.spec.component.endpoint.Consumer\",\n" +
                    "        \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer\",\n" +
                    "        \"configurationId\": \"wriggle-im-inboundRequestConsumer\",\n" +
                    "        \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Inbound Flow_JMS Consumer_1165847135_I\",\n" +
                    "        \"decorators\": [\n" +
                    "          {\n" +
                    "            \"type\": \"Wiretap\",\n" +
                    "            \"name\": \"BEFORE JMS Consumer\",\n" +
                    "            \"configurationId\": \"39\",\n" +
                    "            \"configurable\": true\n" +
                    "          }\n" +
                    "        ],\n" +
                    "        \"configurable\": true\n" +
                    "      },\n" +
                    "      \"transitions\": [\n" +
                    "        {\n" +
                    "          \"from\": \"Internal Request to JSON Converter\",\n" +
                    "          \"to\": \"Internal Request JMS Producer\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"Inbound Request Splitter\",\n" +
                    "          \"to\": \"Internal Request to JSON Converter\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"JSON to Wriggle Inbound Request Converter\",\n" +
                    "          \"to\": \"Inbound Request Splitter\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"JMS Consumer\",\n" +
                    "          \"to\": \"JSON to Wriggle Inbound Request Converter\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"flowElements\": [\n" +
                    "        {\n" +
                    "          \"componentName\": \"Internal Request JMS Producer\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Producer\",\n" +
                    "          \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer\",\n" +
                    "          \"configurationId\": \"wriggle-im-splitInboundRequestProducer\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Inbound Flow_Internal Request JMS Producer_1165847135_I\",\n" +
                    "          \"decorators\": [\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"BEFORE Internal Request JMS Producer\",\n" +
                    "              \"configurationId\": \"1\",\n" +
                    "              \"configurable\": true\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"configurable\": true\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"Internal Request to JSON Converter\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.transformation.Converter\",\n" +
                    "          \"implementingClass\": \"au.com.waddle.wriggle.components.converter.WriggleInternalRequestToJsonConverter\",\n" +
                    "          \"configurationId\": null,\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Inbound Flow_Internal Request to JSON Converter_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": false\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"Inbound Request Splitter\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.splitting.Splitter\",\n" +
                    "          \"implementingClass\": \"au.com.waddle.wriggle.components.splitter.InboundRequestSplitter\",\n" +
                    "          \"configurationId\": null,\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Inbound Flow_Inbound Request Splitter_-91481914_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": false\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"JSON to Wriggle Inbound Request Converter\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.transformation.Converter\",\n" +
                    "          \"implementingClass\": \"au.com.waddle.wriggle.components.converter.InboundJsonToWriggleInboundRequestConverter\",\n" +
                    "          \"configurationId\": null,\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Inbound Flow_JSON to Wriggle Inbound Request Converter_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": false\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"JMS Consumer\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Consumer\",\n" +
                    "          \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer\",\n" +
                    "          \"configurationId\": \"wriggle-im-inboundRequestConsumer\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Inbound Flow_JMS Consumer_1165847135_I\",\n" +
                    "          \"decorators\": [\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"BEFORE JMS Consumer\",\n" +
                    "              \"configurationId\": \"39\",\n" +
                    "              \"configurable\": true\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"configurable\": true\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"configurationId\": \"wriggle-im-Wriggle Request Inbound Flow\"\n" +
                    "    }")
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .withStatus(200)
            ));
        Optional<FlowMetaData> result = uut.getFlowMetadata(contexBaseUrl,"moduleName","flowName");
        assertEquals("Flow name equals", "Wriggle Request Inbound Flow", result.get().getName());
        assertEquals("Number of elements is 5",5, result.get().getFlowElements().size());
    }

    @Test
    public void getFlowMetaData_Returns404()
    {
        stubFor(get(urlEqualTo("/rest/metadata/flow/moduleName/flowName"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()

                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .withStatus(404)
            ));
        Optional<FlowMetaData> result = uut.getFlowMetadata(contexBaseUrl,"moduleName", "flowName");
        assertEquals(false, result.isPresent());

    }

    @Test
    public void getModuleMetaData()
    {
        stubFor(get(urlEqualTo("/rest/metadata/module/moduleName"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
                .withBody("{\n" +
                    "  \"url\": \"http://localhost:8099/wriggle-im\",\n" +
                    "  \"name\": \"wriggle-im\",\n" +
                    "  \"description\": \"Wriggle Accounting\",\n" +
                    "  \"version\": null,\n" +
                    "  \"flows\": [\n" +
                    "    {\n" +
                    "      \"name\": \"Wriggle Request Inbound Flow\",\n" +
                    "      \"consumer\": {\n" +
                    "        \"componentName\": \"JMS Consumer\",\n" +
                    "        \"description\": null,\n" +
                    "        \"componentType\": \"org.ikasan.spec.component.endpoint.Consumer\",\n" +
                    "        \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer\",\n" +
                    "        \"configurationId\": \"wriggle-im-inboundRequestConsumer\",\n" +
                    "        \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Inbound Flow_JMS Consumer_1165847135_I\",\n" +
                    "        \"decorators\": [\n" +
                    "          {\n" +
                    "            \"type\": \"Wiretap\",\n" +
                    "            \"name\": \"BEFORE JMS Consumer\",\n" +
                    "            \"configurationId\": \"39\",\n" +
                    "            \"configurable\": true\n" +
                    "          }\n" +
                    "        ],\n" +
                    "        \"configurable\": true\n" +
                    "      },\n" +
                    "      \"transitions\": [\n" +
                    "        {\n" +
                    "          \"from\": \"Internal Request to JSON Converter\",\n" +
                    "          \"to\": \"Internal Request JMS Producer\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"Inbound Request Splitter\",\n" +
                    "          \"to\": \"Internal Request to JSON Converter\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"JSON to Wriggle Inbound Request Converter\",\n" +
                    "          \"to\": \"Inbound Request Splitter\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"JMS Consumer\",\n" +
                    "          \"to\": \"JSON to Wriggle Inbound Request Converter\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"flowElements\": [\n" +
                    "        {\n" +
                    "          \"componentName\": \"Internal Request JMS Producer\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Producer\",\n" +
                    "          \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer\",\n" +
                    "          \"configurationId\": \"wriggle-im-splitInboundRequestProducer\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Inbound Flow_Internal Request JMS Producer_1165847135_I\",\n" +
                    "          \"decorators\": [\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"BEFORE Internal Request JMS Producer\",\n" +
                    "              \"configurationId\": \"1\",\n" +
                    "              \"configurable\": true\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"configurable\": true\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"Internal Request to JSON Converter\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.transformation.Converter\",\n" +
                    "          \"implementingClass\": \"au.com.waddle.wriggle.components.converter.WriggleInternalRequestToJsonConverter\",\n" +
                    "          \"configurationId\": null,\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Inbound Flow_Internal Request to JSON Converter_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": false\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"Inbound Request Splitter\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.splitting.Splitter\",\n" +
                    "          \"implementingClass\": \"au.com.waddle.wriggle.components.splitter.InboundRequestSplitter\",\n" +
                    "          \"configurationId\": null,\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Inbound Flow_Inbound Request Splitter_-91481914_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": false\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"JSON to Wriggle Inbound Request Converter\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.transformation.Converter\",\n" +
                    "          \"implementingClass\": \"au.com.waddle.wriggle.components.converter.InboundJsonToWriggleInboundRequestConverter\",\n" +
                    "          \"configurationId\": null,\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Inbound Flow_JSON to Wriggle Inbound Request Converter_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": false\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"JMS Consumer\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Consumer\",\n" +
                    "          \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer\",\n" +
                    "          \"configurationId\": \"wriggle-im-inboundRequestConsumer\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Inbound Flow_JMS Consumer_1165847135_I\",\n" +
                    "          \"decorators\": [\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"BEFORE JMS Consumer\",\n" +
                    "              \"configurationId\": \"39\",\n" +
                    "              \"configurable\": true\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"configurable\": true\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"configurationId\": \"wriggle-im-Wriggle Request Inbound Flow\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"name\": \"Wriggle Request Router Flow\",\n" +
                    "      \"consumer\": {\n" +
                    "        \"componentName\": \"JMS Consumer\",\n" +
                    "        \"description\": null,\n" +
                    "        \"componentType\": \"org.ikasan.spec.component.endpoint.Consumer\",\n" +
                    "        \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer\",\n" +
                    "        \"configurationId\": \"wriggle-im-routerFlowConsumer\",\n" +
                    "        \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Router Flow_JMS Consumer_1165847135_I\",\n" +
                    "        \"decorators\": [\n" +
                    "          {\n" +
                    "            \"type\": \"Wiretap\",\n" +
                    "            \"name\": \"BEFORE JMS Consumer\",\n" +
                    "            \"configurationId\": \"11\",\n" +
                    "            \"configurable\": true\n" +
                    "          },\n" +
                    "          {\n" +
                    "            \"type\": \"LogWiretap\",\n" +
                    "            \"name\": \"BEFORE JMS Consumer\",\n" +
                    "            \"configurationId\": \"40\",\n" +
                    "            \"configurable\": false\n" +
                    "          },\n" +
                    "          {\n" +
                    "            \"type\": \"LogWiretap\",\n" +
                    "            \"name\": \"BEFORE JMS Consumer\",\n" +
                    "            \"configurationId\": \"41\",\n" +
                    "            \"configurable\": false\n" +
                    "          },\n" +
                    "          {\n" +
                    "            \"type\": \"Wiretap\",\n" +
                    "            \"name\": \"AFTER JMS Consumer\",\n" +
                    "            \"configurationId\": \"12\",\n" +
                    "            \"configurable\": true\n" +
                    "          },\n" +
                    "          {\n" +
                    "            \"type\": \"LogWiretap\",\n" +
                    "            \"name\": \"AFTER JMS Consumer\",\n" +
                    "            \"configurationId\": \"42\",\n" +
                    "            \"configurable\": false\n" +
                    "          }\n" +
                    "        ],\n" +
                    "        \"configurable\": true\n" +
                    "      },\n" +
                    "      \"transitions\": [\n" +
                    "        {\n" +
                    "          \"from\": \"Wriggle Company Inbound Request to JSON Converter\",\n" +
                    "          \"to\": \"Company Request Producer\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"Request Type Router\",\n" +
                    "          \"to\": \"Wriggle Company Inbound Request to JSON Converter\",\n" +
                    "          \"name\": \"company\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"Wriggle Account Inbound Request to JSON Converter\",\n" +
                    "          \"to\": \"Bank Request Producer\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"Request Type Router\",\n" +
                    "          \"to\": \"Wriggle Account Inbound Request to JSON Converter\",\n" +
                    "          \"name\": \"bank\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"Wriggle Customer Inbound Request to JSON Converter\",\n" +
                    "          \"to\": \"Customer Request Producer\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"Request Type Router\",\n" +
                    "          \"to\": \"Wriggle Customer Inbound Request to JSON Converter\",\n" +
                    "          \"name\": \"customer\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"Wriggle Invoice Inbound Request to JSON Converter\",\n" +
                    "          \"to\": \"Invoice Request Producer\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"Request Type Router\",\n" +
                    "          \"to\": \"Wriggle Invoice Inbound Request to JSON Converter\",\n" +
                    "          \"name\": \"invoice\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"Wriggle Ledger Inbound Request to JSON Converter\",\n" +
                    "          \"to\": \"Ledger Request Producer\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"Request Type Router\",\n" +
                    "          \"to\": \"Wriggle Ledger Inbound Request to JSON Converter\",\n" +
                    "          \"name\": \"ledger\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"Request Type Router\",\n" +
                    "          \"to\": \"Ignore Unknown Request\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"Exception Generating Broker\",\n" +
                    "          \"to\": \"Request Type Router\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"JSON to Wriggle Inbound Request Converter\",\n" +
                    "          \"to\": \"Exception Generating Broker\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"JMS Consumer\",\n" +
                    "          \"to\": \"JSON to Wriggle Inbound Request Converter\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"flowElements\": [\n" +
                    "        {\n" +
                    "          \"componentName\": \"Company Request Producer\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Producer\",\n" +
                    "          \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer\",\n" +
                    "          \"configurationId\": \"wriggle-im-companyRequestProducer\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Router Flow_Company Request Producer_1165847135_I\",\n" +
                    "          \"decorators\": [\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"BEFORE Company Request Producer\",\n" +
                    "              \"configurationId\": \"32\",\n" +
                    "              \"configurable\": true\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"AFTER Company Request Producer\",\n" +
                    "              \"configurationId\": \"4\",\n" +
                    "              \"configurable\": true\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"AFTER Company Request Producer\",\n" +
                    "              \"configurationId\": \"22\",\n" +
                    "              \"configurable\": true\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"AFTER Company Request Producer\",\n" +
                    "              \"configurationId\": \"33\",\n" +
                    "              \"configurable\": true\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"configurable\": true\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"Wriggle Company Inbound Request to JSON Converter\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.transformation.Converter\",\n" +
                    "          \"implementingClass\": \"au.com.waddle.wriggle.components.converter.WriggleInternalRequestToJsonConverter\",\n" +
                    "          \"configurationId\": null,\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Router Flow_Wriggle Company Inbound Request to JSON Converter_1165847135_I\",\n" +
                    "          \"decorators\": [\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"BEFORE Wriggle Company Inbound Request to JSON Converter\",\n" +
                    "              \"configurationId\": \"26\",\n" +
                    "              \"configurable\": true\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"AFTER Wriggle Company Inbound Request to JSON Converter\",\n" +
                    "              \"configurationId\": \"27\",\n" +
                    "              \"configurable\": true\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"configurable\": false\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"Bank Request Producer\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Producer\",\n" +
                    "          \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer\",\n" +
                    "          \"configurationId\": \"wriggle-im-bankRequestProducer\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Router Flow_Bank Request Producer_1165847135_I\",\n" +
                    "          \"decorators\": [\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"BEFORE Bank Request Producer\",\n" +
                    "              \"configurationId\": \"20\",\n" +
                    "              \"configurable\": true\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"AFTER Bank Request Producer\",\n" +
                    "              \"configurationId\": \"21\",\n" +
                    "              \"configurable\": true\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"configurable\": true\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"Wriggle Account Inbound Request to JSON Converter\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.transformation.Converter\",\n" +
                    "          \"implementingClass\": \"au.com.waddle.wriggle.components.converter.WriggleInternalRequestToJsonConverter\",\n" +
                    "          \"configurationId\": null,\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Router Flow_Wriggle Account Inbound Request to JSON Converter_1165847135_I\",\n" +
                    "          \"decorators\": [\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"BEFORE Wriggle Account Inbound Request to JSON Converter\",\n" +
                    "              \"configurationId\": \"37\",\n" +
                    "              \"configurable\": true\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"AFTER Wriggle Account Inbound Request to JSON Converter\",\n" +
                    "              \"configurationId\": \"38\",\n" +
                    "              \"configurable\": true\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"configurable\": false\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"Customer Request Producer\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Producer\",\n" +
                    "          \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer\",\n" +
                    "          \"configurationId\": \"wriggle-im-customerRequestProducer\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Router Flow_Customer Request Producer_1165847135_I\",\n" +
                    "          \"decorators\": [\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"BEFORE Customer Request Producer\",\n" +
                    "              \"configurationId\": \"8\",\n" +
                    "              \"configurable\": true\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"AFTER Customer Request Producer\",\n" +
                    "              \"configurationId\": \"25\",\n" +
                    "              \"configurable\": true\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"configurable\": true\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"Wriggle Customer Inbound Request to JSON Converter\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.transformation.Converter\",\n" +
                    "          \"implementingClass\": \"au.com.waddle.wriggle.components.converter.WriggleInternalRequestToJsonConverter\",\n" +
                    "          \"configurationId\": null,\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Router Flow_Wriggle Customer Inbound Request to JSON Converter_1165847135_I\",\n" +
                    "          \"decorators\": [\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"BEFORE Wriggle Customer Inbound Request to JSON Converter\",\n" +
                    "              \"configurationId\": \"6\",\n" +
                    "              \"configurable\": true\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"AFTER Wriggle Customer Inbound Request to JSON Converter\",\n" +
                    "              \"configurationId\": \"36\",\n" +
                    "              \"configurable\": true\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"configurable\": false\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"Invoice Request Producer\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Producer\",\n" +
                    "          \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer\",\n" +
                    "          \"configurationId\": \"wriggle-im-invoiceRequestProducer\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Router Flow_Invoice Request Producer_1165847135_I\",\n" +
                    "          \"decorators\": [\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"BEFORE Invoice Request Producer\",\n" +
                    "              \"configurationId\": \"23\",\n" +
                    "              \"configurable\": true\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"AFTER Invoice Request Producer\",\n" +
                    "              \"configurationId\": \"5\",\n" +
                    "              \"configurable\": true\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"AFTER Invoice Request Producer\",\n" +
                    "              \"configurationId\": \"24\",\n" +
                    "              \"configurable\": true\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"configurable\": true\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"Wriggle Invoice Inbound Request to JSON Converter\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.transformation.Converter\",\n" +
                    "          \"implementingClass\": \"au.com.waddle.wriggle.components.converter.WriggleInternalRequestToJsonConverter\",\n" +
                    "          \"configurationId\": null,\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Router Flow_Wriggle Invoice Inbound Request to JSON Converter_1165847135_I\",\n" +
                    "          \"decorators\": [\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"BEFORE Wriggle Invoice Inbound Request to JSON Converter\",\n" +
                    "              \"configurationId\": \"28\",\n" +
                    "              \"configurable\": true\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"BEFORE Wriggle Invoice Inbound Request to JSON Converter\",\n" +
                    "              \"configurationId\": \"34\",\n" +
                    "              \"configurable\": true\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"AFTER Wriggle Invoice Inbound Request to JSON Converter\",\n" +
                    "              \"configurationId\": \"29\",\n" +
                    "              \"configurable\": true\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"AFTER Wriggle Invoice Inbound Request to JSON Converter\",\n" +
                    "              \"configurationId\": \"35\",\n" +
                    "              \"configurable\": true\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"configurable\": false\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"Ledger Request Producer\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Producer\",\n" +
                    "          \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer\",\n" +
                    "          \"configurationId\": \"wriggle-im-ledgeRequestProducer\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Router Flow_Ledger Request Producer_1165847135_I\",\n" +
                    "          \"decorators\": [\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"BEFORE Ledger Request Producer\",\n" +
                    "              \"configurationId\": \"18\",\n" +
                    "              \"configurable\": true\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"AFTER Ledger Request Producer\",\n" +
                    "              \"configurationId\": \"19\",\n" +
                    "              \"configurable\": true\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"configurable\": true\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"Wriggle Ledger Inbound Request to JSON Converter\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.transformation.Converter\",\n" +
                    "          \"implementingClass\": \"au.com.waddle.wriggle.components.converter.WriggleInternalRequestToJsonConverter\",\n" +
                    "          \"configurationId\": null,\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Router Flow_Wriggle Ledger Inbound Request to JSON Converter_1165847135_I\",\n" +
                    "          \"decorators\": [\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"BEFORE Wriggle Ledger Inbound Request to JSON Converter\",\n" +
                    "              \"configurationId\": \"3\",\n" +
                    "              \"configurable\": true\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"AFTER Wriggle Ledger Inbound Request to JSON Converter\",\n" +
                    "              \"configurationId\": \"7\",\n" +
                    "              \"configurable\": true\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"configurable\": false\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"Ignore Unknown Request\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Producer\",\n" +
                    "          \"implementingClass\": \"org.ikasan.component.endpoint.util.producer.DevNull\",\n" +
                    "          \"configurationId\": null,\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Router Flow_Ignore Unknown Request_1165847135_I\",\n" +
                    "          \"decorators\": [\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"BEFORE Ignore Unknown Request\",\n" +
                    "              \"configurationId\": \"13\",\n" +
                    "              \"configurable\": true\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"AFTER Ignore Unknown Request\",\n" +
                    "              \"configurationId\": \"14\",\n" +
                    "              \"configurable\": true\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"configurable\": false\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"Request Type Router\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.routing.SingleRecipientRouter\",\n" +
                    "          \"implementingClass\": \"au.com.waddle.wriggle.components.router.RequestTypeRouter\",\n" +
                    "          \"configurationId\": null,\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Router Flow_Request Type Router_1165847135_I\",\n" +
                    "          \"decorators\": [\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"BEFORE Request Type Router\",\n" +
                    "              \"configurationId\": \"9\",\n" +
                    "              \"configurable\": true\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"AFTER Request Type Router\",\n" +
                    "              \"configurationId\": \"10\",\n" +
                    "              \"configurable\": true\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"configurable\": false\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"Exception Generating Broker\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Broker\",\n" +
                    "          \"implementingClass\": \"au.com.waddle.wriggle.components.broker.ExceptionGeneratingBroker\",\n" +
                    "          \"configurationId\": \"wriggle-im-exceptiion-broker\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Router Flow_Exception Generating Broker_1165847135_I\",\n" +
                    "          \"decorators\": [\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"BEFORE Exception Generating Broker\",\n" +
                    "              \"configurationId\": \"30\",\n" +
                    "              \"configurable\": true\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"AFTER Exception Generating Broker\",\n" +
                    "              \"configurationId\": \"15\",\n" +
                    "              \"configurable\": true\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"AFTER Exception Generating Broker\",\n" +
                    "              \"configurationId\": \"31\",\n" +
                    "              \"configurable\": true\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"configurable\": true\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"JSON to Wriggle Inbound Request Converter\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.transformation.Converter\",\n" +
                    "          \"implementingClass\": \"au.com.waddle.wriggle.components.converter.JsonToWriggleInternalRequestConverter\",\n" +
                    "          \"configurationId\": null,\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Router Flow_JSON to Wriggle Inbound Request Converter_1165847135_I\",\n" +
                    "          \"decorators\": [\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"BEFORE JSON to Wriggle Inbound Request Converter\",\n" +
                    "              \"configurationId\": \"16\",\n" +
                    "              \"configurable\": true\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"AFTER JSON to Wriggle Inbound Request Converter\",\n" +
                    "              \"configurationId\": \"17\",\n" +
                    "              \"configurable\": true\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"configurable\": false\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"JMS Consumer\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Consumer\",\n" +
                    "          \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer\",\n" +
                    "          \"configurationId\": \"wriggle-im-routerFlowConsumer\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Request Router Flow_JMS Consumer_1165847135_I\",\n" +
                    "          \"decorators\": [\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"BEFORE JMS Consumer\",\n" +
                    "              \"configurationId\": \"11\",\n" +
                    "              \"configurable\": true\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"type\": \"LogWiretap\",\n" +
                    "              \"name\": \"BEFORE JMS Consumer\",\n" +
                    "              \"configurationId\": \"40\",\n" +
                    "              \"configurable\": false\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"type\": \"LogWiretap\",\n" +
                    "              \"name\": \"BEFORE JMS Consumer\",\n" +
                    "              \"configurationId\": \"41\",\n" +
                    "              \"configurable\": false\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"AFTER JMS Consumer\",\n" +
                    "              \"configurationId\": \"12\",\n" +
                    "              \"configurable\": true\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"type\": \"LogWiretap\",\n" +
                    "              \"name\": \"AFTER JMS Consumer\",\n" +
                    "              \"configurationId\": \"42\",\n" +
                    "              \"configurable\": false\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"configurable\": true\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"configurationId\": \"wriggle-im-Wriggle Request Router Flow\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"name\": \"Wriggle Customer HTTP Request Flow\",\n" +
                    "      \"consumer\": {\n" +
                    "        \"componentName\": \"JMS Consumer\",\n" +
                    "        \"description\": null,\n" +
                    "        \"componentType\": \"org.ikasan.spec.component.endpoint.Consumer\",\n" +
                    "        \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer\",\n" +
                    "        \"configurationId\": \"wriggle-im-customerHttpRequestFlowConsumer\",\n" +
                    "        \"invokerConfigurationId\": \"wriggle-im_Wriggle Customer HTTP Request Flow_JMS Consumer_1165847135_I\",\n" +
                    "        \"decorators\": null,\n" +
                    "        \"configurable\": true\n" +
                    "      },\n" +
                    "      \"transitions\": [\n" +
                    "        {\n" +
                    "          \"from\": \"HTTP Request Broker\",\n" +
                    "          \"to\": \"Public Customer HTTP Request JMS Producer\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"JSON to Wriggle Inbound Request Converter\",\n" +
                    "          \"to\": \"HTTP Request Broker\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"JMS Consumer\",\n" +
                    "          \"to\": \"JSON to Wriggle Inbound Request Converter\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"flowElements\": [\n" +
                    "        {\n" +
                    "          \"componentName\": \"Public Customer HTTP Request JMS Producer\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Producer\",\n" +
                    "          \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer\",\n" +
                    "          \"configurationId\": \"wriggle-im-customerHttpRequestFlowProducer\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Customer HTTP Request Flow_Public Customer HTTP Request JMS Producer_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": true\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"HTTP Request Broker\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Broker\",\n" +
                    "          \"implementingClass\": \"au.com.waddle.wriggle.components.broker.HttpRequestBroker\",\n" +
                    "          \"configurationId\": \"wriggle-im-customerHttpRequestFlowHTTPRequestBroker\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Customer HTTP Request Flow_HTTP Request Broker_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": true\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"JSON to Wriggle Inbound Request Converter\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.transformation.Converter\",\n" +
                    "          \"implementingClass\": \"au.com.waddle.wriggle.components.converter.JsonToWriggleInternalRequestConverter\",\n" +
                    "          \"configurationId\": null,\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Customer HTTP Request Flow_JSON to Wriggle Inbound Request Converter_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": false\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"JMS Consumer\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Consumer\",\n" +
                    "          \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer\",\n" +
                    "          \"configurationId\": \"wriggle-im-customerHttpRequestFlowConsumer\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Customer HTTP Request Flow_JMS Consumer_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": true\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"configurationId\": \"wriggle-im-Wriggle Customer HTTP Request Flow\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"name\": \"Wriggle Ledger HTTP Request Flow\",\n" +
                    "      \"consumer\": {\n" +
                    "        \"componentName\": \"JMS Consumer\",\n" +
                    "        \"description\": null,\n" +
                    "        \"componentType\": \"org.ikasan.spec.component.endpoint.Consumer\",\n" +
                    "        \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer\",\n" +
                    "        \"configurationId\": \"wriggle-im-ledgerHttpRequestFlowConsumer\",\n" +
                    "        \"invokerConfigurationId\": \"wriggle-im_Wriggle Ledger HTTP Request Flow_JMS Consumer_1165847135_I\",\n" +
                    "        \"decorators\": null,\n" +
                    "        \"configurable\": true\n" +
                    "      },\n" +
                    "      \"transitions\": [\n" +
                    "        {\n" +
                    "          \"from\": \"HTTP Request Broker\",\n" +
                    "          \"to\": \"Public Ledger HTTP Request JMS Producer\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"JSON to Wriggle Inbound Request Converter\",\n" +
                    "          \"to\": \"HTTP Request Broker\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"JMS Consumer\",\n" +
                    "          \"to\": \"JSON to Wriggle Inbound Request Converter\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"flowElements\": [\n" +
                    "        {\n" +
                    "          \"componentName\": \"Public Ledger HTTP Request JMS Producer\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Producer\",\n" +
                    "          \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer\",\n" +
                    "          \"configurationId\": \"wriggle-im-ledgerHttpRequestFlowProducer\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Ledger HTTP Request Flow_Public Ledger HTTP Request JMS Producer_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": true\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"HTTP Request Broker\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Broker\",\n" +
                    "          \"implementingClass\": \"au.com.waddle.wriggle.components.broker.HttpRequestBroker\",\n" +
                    "          \"configurationId\": \"wriggle-im-ledgerHttpRequestFlowHTTPRequestBroker\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Ledger HTTP Request Flow_HTTP Request Broker_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": true\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"JSON to Wriggle Inbound Request Converter\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.transformation.Converter\",\n" +
                    "          \"implementingClass\": \"au.com.waddle.wriggle.components.converter.JsonToWriggleInternalRequestConverter\",\n" +
                    "          \"configurationId\": null,\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Ledger HTTP Request Flow_JSON to Wriggle Inbound Request Converter_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": false\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"JMS Consumer\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Consumer\",\n" +
                    "          \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer\",\n" +
                    "          \"configurationId\": \"wriggle-im-ledgerHttpRequestFlowConsumer\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Ledger HTTP Request Flow_JMS Consumer_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": true\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"configurationId\": \"wriggle-im-Wriggle Ledger HTTP Request Flow\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"name\": \"Wriggle Company HTTP Request Flow\",\n" +
                    "      \"consumer\": {\n" +
                    "        \"componentName\": \"JMS Consumer\",\n" +
                    "        \"description\": null,\n" +
                    "        \"componentType\": \"org.ikasan.spec.component.endpoint.Consumer\",\n" +
                    "        \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer\",\n" +
                    "        \"configurationId\": \"wriggle-im-companyHttpRequestFlowConsumer\",\n" +
                    "        \"invokerConfigurationId\": \"wriggle-im_Wriggle Company HTTP Request Flow_JMS Consumer_1165847135_I\",\n" +
                    "        \"decorators\": null,\n" +
                    "        \"configurable\": true\n" +
                    "      },\n" +
                    "      \"transitions\": [\n" +
                    "        {\n" +
                    "          \"from\": \"HTTP Request Broker\",\n" +
                    "          \"to\": \"Public Company HTTP Request JMS Producer\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"JSON to Wriggle Inbound Request Converter\",\n" +
                    "          \"to\": \"HTTP Request Broker\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"JMS Consumer\",\n" +
                    "          \"to\": \"JSON to Wriggle Inbound Request Converter\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"flowElements\": [\n" +
                    "        {\n" +
                    "          \"componentName\": \"Public Company HTTP Request JMS Producer\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Producer\",\n" +
                    "          \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer\",\n" +
                    "          \"configurationId\": \"wriggle-im-companyHttpRequestFlowProducer\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Company HTTP Request Flow_Public Company HTTP Request JMS Producer_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": true\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"HTTP Request Broker\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Broker\",\n" +
                    "          \"implementingClass\": \"au.com.waddle.wriggle.components.broker.HttpRequestBroker\",\n" +
                    "          \"configurationId\": \"wriggle-im-companyHttpRequestFlowHTTPRequestBroker\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Company HTTP Request Flow_HTTP Request Broker_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": true\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"JSON to Wriggle Inbound Request Converter\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.transformation.Converter\",\n" +
                    "          \"implementingClass\": \"au.com.waddle.wriggle.components.converter.JsonToWriggleInternalRequestConverter\",\n" +
                    "          \"configurationId\": null,\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Company HTTP Request Flow_JSON to Wriggle Inbound Request Converter_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": false\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"JMS Consumer\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Consumer\",\n" +
                    "          \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer\",\n" +
                    "          \"configurationId\": \"wriggle-im-companyHttpRequestFlowConsumer\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Company HTTP Request Flow_JMS Consumer_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": true\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"configurationId\": \"wriggle-im-Wriggle Company HTTP Request Flow\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"name\": \"Wriggle Account HTTP Request Flow\",\n" +
                    "      \"consumer\": {\n" +
                    "        \"componentName\": \"JMS Consumer\",\n" +
                    "        \"description\": null,\n" +
                    "        \"componentType\": \"org.ikasan.spec.component.endpoint.Consumer\",\n" +
                    "        \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer\",\n" +
                    "        \"configurationId\": \"wriggle-im-accountHttpRequestFlowConsumer\",\n" +
                    "        \"invokerConfigurationId\": \"wriggle-im_Wriggle Account HTTP Request Flow_JMS Consumer_1165847135_I\",\n" +
                    "        \"decorators\": null,\n" +
                    "        \"configurable\": true\n" +
                    "      },\n" +
                    "      \"transitions\": [\n" +
                    "        {\n" +
                    "          \"from\": \"HTTP Request Broker\",\n" +
                    "          \"to\": \"Public Account HTTP Request JMS Producer\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"JSON to Wriggle Inbound Request Converter\",\n" +
                    "          \"to\": \"HTTP Request Broker\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"JMS Consumer\",\n" +
                    "          \"to\": \"JSON to Wriggle Inbound Request Converter\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"flowElements\": [\n" +
                    "        {\n" +
                    "          \"componentName\": \"Public Account HTTP Request JMS Producer\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Producer\",\n" +
                    "          \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer\",\n" +
                    "          \"configurationId\": \"wriggle-im-accountHttpRequestFlowProducer\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Account HTTP Request Flow_Public Account HTTP Request JMS Producer_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": true\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"HTTP Request Broker\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Broker\",\n" +
                    "          \"implementingClass\": \"au.com.waddle.wriggle.components.broker.HttpRequestBroker\",\n" +
                    "          \"configurationId\": \"wriggle-im-accountHttpRequestFlowHTTPRequestBroker\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Account HTTP Request Flow_HTTP Request Broker_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": true\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"JSON to Wriggle Inbound Request Converter\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.transformation.Converter\",\n" +
                    "          \"implementingClass\": \"au.com.waddle.wriggle.components.converter.JsonToWriggleInternalRequestConverter\",\n" +
                    "          \"configurationId\": null,\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Account HTTP Request Flow_JSON to Wriggle Inbound Request Converter_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": false\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"JMS Consumer\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Consumer\",\n" +
                    "          \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer\",\n" +
                    "          \"configurationId\": \"wriggle-im-accountHttpRequestFlowConsumer\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Account HTTP Request Flow_JMS Consumer_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": true\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"configurationId\": \"wriggle-im-Wriggle Account HTTP Request Flow\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"name\": \"Wriggle Invoice HTTP Request Flow\",\n" +
                    "      \"consumer\": {\n" +
                    "        \"componentName\": \"JMS Consumer\",\n" +
                    "        \"description\": null,\n" +
                    "        \"componentType\": \"org.ikasan.spec.component.endpoint.Consumer\",\n" +
                    "        \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer\",\n" +
                    "        \"configurationId\": \"wriggle-im-InvoiceHttpRequestFlowConsumer\",\n" +
                    "        \"invokerConfigurationId\": \"wriggle-im_Wriggle Invoice HTTP Request Flow_JMS Consumer_1165847135_I\",\n" +
                    "        \"decorators\": null,\n" +
                    "        \"configurable\": true\n" +
                    "      },\n" +
                    "      \"transitions\": [\n" +
                    "        {\n" +
                    "          \"from\": \"HTTP Request Broker\",\n" +
                    "          \"to\": \"Public Invoice HTTP Request JMS Producer\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"JSON to Wriggle Inbound Request Converter\",\n" +
                    "          \"to\": \"HTTP Request Broker\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"from\": \"JMS Consumer\",\n" +
                    "          \"to\": \"JSON to Wriggle Inbound Request Converter\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"flowElements\": [\n" +
                    "        {\n" +
                    "          \"componentName\": \"Public Invoice HTTP Request JMS Producer\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Producer\",\n" +
                    "          \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer\",\n" +
                    "          \"configurationId\": \"wriggle-im-InvoiceHttpRequestFlowProducer\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Invoice HTTP Request Flow_Public Invoice HTTP Request JMS Producer_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": true\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"HTTP Request Broker\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Broker\",\n" +
                    "          \"implementingClass\": \"au.com.waddle.wriggle.components.broker.HttpRequestBroker\",\n" +
                    "          \"configurationId\": \"wriggle-im-InvoiceHttpRequestFlowHTTPRequestBroker\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Invoice HTTP Request Flow_HTTP Request Broker_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": true\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"JSON to Wriggle Inbound Request Converter\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.transformation.Converter\",\n" +
                    "          \"implementingClass\": \"au.com.waddle.wriggle.components.converter.JsonToWriggleInternalRequestConverter\",\n" +
                    "          \"configurationId\": null,\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Invoice HTTP Request Flow_JSON to Wriggle Inbound Request Converter_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": false\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"JMS Consumer\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Consumer\",\n" +
                    "          \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer\",\n" +
                    "          \"configurationId\": \"wriggle-im-InvoiceHttpRequestFlowConsumer\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Wriggle Invoice HTTP Request Flow_JMS Consumer_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": true\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"configurationId\": \"wriggle-im-Wriggle Invoice HTTP Request Flow\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"name\": \"Scheduled Flow\",\n" +
                    "      \"consumer\": {\n" +
                    "        \"componentName\": \"Scheduled Consumer\",\n" +
                    "        \"description\": null,\n" +
                    "        \"componentType\": \"org.ikasan.spec.component.endpoint.Consumer\",\n" +
                    "        \"implementingClass\": \"org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer\",\n" +
                    "        \"configurationId\": \"wriggle-im-scheduledFlowConsumer\",\n" +
                    "        \"invokerConfigurationId\": \"wriggle-im_Scheduled Flow_Scheduled Consumer_1165847135_I\",\n" +
                    "        \"decorators\": null,\n" +
                    "        \"configurable\": true\n" +
                    "      },\n" +
                    "      \"transitions\": [\n" +
                    "        {\n" +
                    "          \"from\": \"Scheduled Consumer\",\n" +
                    "          \"to\": \"Scheduled Inbound JMS Producer\",\n" +
                    "          \"name\": \"default\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"flowElements\": [\n" +
                    "        {\n" +
                    "          \"componentName\": \"Scheduled Inbound JMS Producer\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Producer\",\n" +
                    "          \"implementingClass\": \"org.ikasan.component.endpoint.jms.spring.producer.ArjunaJmsTemplateProducer\",\n" +
                    "          \"configurationId\": \"wriggle-im-inboundJmsProducer\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Scheduled Flow_Scheduled Inbound JMS Producer_1165847135_I\",\n" +
                    "          \"decorators\": [\n" +
                    "            {\n" +
                    "              \"type\": \"Wiretap\",\n" +
                    "              \"name\": \"BEFORE Scheduled Inbound JMS Producer\",\n" +
                    "              \"configurationId\": \"2\",\n" +
                    "              \"configurable\": true\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"configurable\": true\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"componentName\": \"Scheduled Consumer\",\n" +
                    "          \"description\": null,\n" +
                    "          \"componentType\": \"org.ikasan.spec.component.endpoint.Consumer\",\n" +
                    "          \"implementingClass\": \"org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer\",\n" +
                    "          \"configurationId\": \"wriggle-im-scheduledFlowConsumer\",\n" +
                    "          \"invokerConfigurationId\": \"wriggle-im_Scheduled Flow_Scheduled Consumer_1165847135_I\",\n" +
                    "          \"decorators\": null,\n" +
                    "          \"configurable\": true\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"configurationId\": \"wriggle-im-Scheduled Flow\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}")
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .withStatus(200)
            ));
        Optional<ModuleMetaData> result = uut.getModuleMetadata(contexBaseUrl,"moduleName");
        assertEquals("Flow name equals", "wriggle-im", result.get().getName());
        assertEquals("Number of flows is 8",8, result.get().getFlows().size());
    }

    @Test
    public void getModuleMetaData_Returns404()
    {
        stubFor(get(urlEqualTo("/rest/metadata/module/moduleName"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()

                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .withStatus(404)
            ));
        Optional<ModuleMetaData> result = uut.getModuleMetadata(contexBaseUrl,"moduleName");
        assertEquals(false, result.isPresent());

    }
}
