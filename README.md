This example tests:

1. Adding a node to Neo4J
2. Adding a simple property to a node.
3. Creating an index in Neo4J
4. Adding the node, property and value to the index.

The step 4 does not seem to work in 1.8-SNAPSHOT of Neo4J.
What I mean is that when we try to get this node from the index,
it does not return it. So we don't know if adding the node to
index is failing, or getting it from index is failing or both.

It does work in 1.7 of Neo4J.

Perhaps, we are doing something wrong. Or some new code needs to
be written to make this code work.

Submitted this code to Build Doctor, and Peter Neubauer for review
via twitter.
