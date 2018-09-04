package com.bdgx;

import graphql.servlet.GraphQLInvocationInputFactory;
import graphql.servlet.GraphQLObjectMapper;
import graphql.servlet.GraphQLQueryInvoker;
import graphql.servlet.GraphQLWebsocketServlet;

/**
 * @author hai
 * description
 * email hilin2333@gmail.com
 * date 2018/9/3 6:43 PM
 */
public class MyServlet extends GraphQLWebsocketServlet {


    public MyServlet(GraphQLQueryInvoker queryInvoker, GraphQLInvocationInputFactory invocationInputFactory, GraphQLObjectMapper graphQLObjectMapper) {
        super(queryInvoker, invocationInputFactory, graphQLObjectMapper);
    }
}
