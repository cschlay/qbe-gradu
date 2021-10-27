# Example Queries

```xml
<graph>
    <node name="Course" />
</graph>
```

## Node Queries

Show all nodes without properties:
```xml
<graph>
    <node />
</graph>
```

Show all nodes with name without properties:
```xml
<graph>
    <node name="Course" />
</graph>
```

Show all properties:
```xml
<graph>
    <node name="">
        <data />
    </node>
</graph>
```

Limited by attributes
```xml
<graph>
    <node>
        <data key="title" />
    </node>
</graph>
```

Using Regex
```xml
<graph>
    <node>
        <data key="title" type="regex">Introduction to .*</data>
    </node>
</graph>
```

## Visibility Attributes


## Nodes with Edges