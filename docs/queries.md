# Example Queries

```xml
<graph>
    <node name="Course" />
</graph>
```

## Anonymous Nodes

Anonymous nodes are not restricted by "name" -attribute.

### No Attributes

Should show all nodes in the database.

```xml
<graph><node /></graph>
```

### Limited by attributes

Nodes that have attribute value should be included.

```xml
<graph>
    <node>
        <data key="title">Introduction .*</data>
    </node>
</graph>
```


## Visibility Attributes


## Nodes with Edges