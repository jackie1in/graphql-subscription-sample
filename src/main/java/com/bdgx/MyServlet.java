package com.bdgx;

import graphql.servlet.GraphQLInvocationInputFactory;
import graphql.servlet.GraphQLObjectMapper;
import graphql.servlet.GraphQLQueryInvoker;
import graphql.servlet.GraphQLWebsocketServlet;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.util.HashSet;
import java.util.Set;

/**
 * @author hai
 * description
 * email hilin2333@gmail.com
 * date 2018/9/3 6:43 PM
 */
public class MyServlet extends GraphQLWebsocketServlet{
    public static final Set<String> closeSession = new HashSet<>();
    public MyServlet(GraphQLQueryInvoker queryInvoker, GraphQLInvocationInputFactory invocationInputFactory, GraphQLObjectMapper graphQLObjectMapper) {
        super(queryInvoker, invocationInputFactory, graphQLObjectMapper);
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        super.onClose(session, closeReason);
        System.out.println(session.getId());
        closeSession.add(session.getId());
    }
}
