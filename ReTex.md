# ReTex 2.0
This document is used to dump my thoughts around an updated ReTex document system.
Version 2.0 builds upon the fundamental ideas of original sketch and refines them 
to a level of detail that can be implemented.

Name: futex => future of tex(t)

## Naming
Some name changes that needs to be applied to all 1.0 examples:

Current is `\text` or `\meta`, maybe should be `\define[\type text \id section]`?

        \kind => \nature
        \below => \in
        \above => \around
        \bare => \implicit (also allow lists for implicit: [type nature] when used as \define[section text])
        \default => \plain
        \expect => \inline ?


## Natures

The concept of natures means that there are no elements or attributes that are privileges based on their name, their identity, but that any element or attribute can be associated with a certain nature, a role or function which they have when used in a document.
It is their nature, not their name, that makes the system understand what they are used for.
That said, there has to be a set of initial elements and attributes that are virtually defined. 
Virtual as they are defined in terms of or with the help of themselves and the other members of the initial set.
While we can formulate their definitions these elements and attributes had to pre-exist in the system so there is something to formulate with.
In that sense these few elements and attributes are privileges ones.
But this is a mere solution to the hen-egg situation at the origin.

The natures can be grouped in several groups for different purposes.

Content:
* `text` (the defined element is a form of textual content, like heading, section, paragraph, ...; it is meant to be readable - this is the fundamental nature for content)
* `note` (content given for the sake of annotating more information to support information processing, indexing, review, etcetera; differs from layer in that layers only make sense to rendering while note classifies the content as not being part of the main body of text in a way that is universally understood)
* meta: `mark` (aka _anchor_; a named "pointer" to a particular content position in a document, when used as element it wraps around the text that is marked, when used as an attribute the marked position is the beginning of the element that the mark is added to as an attribute)

Presentation:
* meta: `layer` (vaguely: a hint for the rendering where a content is presented, e.g. document body, comment, header, footer, ...)
* meta: `style` (as attribute: a general hint that rendering makes sense of based on the attribute name, basically a way to add key-value data for rendering/presentation, these will always be specific to the presentation used. as element: used to wrap around content to trigger a certain renderer identified by the elements name)

Identity:
* meta: `define` (an elements that defines new elements or attributes OR (when used as attribute) the attribute that references the nature of the defined element or attribute)
* meta: `is-a` (refers to "parent" element or attribute definition(s) which are the basis of the defined element or attribute, it x _is a type of_ y; when used in an element that is not of nature `define` it makes the element of the referred sort)
* meta: `alias` (adds one or more alias names for the defined element or attribute; for example to create an abbr. name to use in inline formatting)
* meta: `ref` (gives one or more references to element or attributes, the assumed type(s) are given through the `accept` nature. For example when `accept` is `define` the reference is to `alias` name(s) of a `define` element)

Validity:
* meta: `accept` (gives a set of elements, is a elements (`*`) or natures (`&`) that is valid as actual value for the defined *attribute*, if used with elements this restricts possible child elements or attributes depending on the natures of the items in the set)
* meta: `must` (what are attributes that must be given in `[...]` to the defined element or attribute)
* meta: `may` (what are attributes that may be given in `[...]` to the defined element or attribute)

Inference:
* meta: `in` (what are direct parents to the defined element/attribute)
* meta: `around` (what are known direct children to the defined element)
* meta: `implicit` (what is assumed in `\[...]` if no keys are given for the defined element/attribute)
* meta: `plain` (what is assumed for line with no type around after the usage of the defined element)
* meta: `inline` (what is assumed for `\[...]` => `\x[...]` in text following the defined element)
 
Interpretation:
* meta: `scanner` used as attribite to change the scanner implementation used

Modularisation:
* meta: `ns` (declare and use namespaces)

Counters:
* meta: `counter` (links a text element to a named counter; on usage of the text element the counter is incremented)
* meta: `reset` (links a text element to a named counter; on usage of the text element the counter is reset)


# Bootstrap
The system is conceptually defined in terms of itself using natures.
The starting point is the nature `define` which allows to bootstrap everything else.

This means while the semantics, what a certain nature means, is static and already defined, 
the elements and attributes used to work with the nature are not "coded" 
but declared based on the fundamental nature `define`
for which the system provides the starting point element `\define`.

While `\define` is not declared but "build in" the declaration mechanism could
have been used to declare it if there wasn't a obvious hen-egg problem doing so.

The following examines this virtual declaration to explain how the system works.
For this the full declaration is build up step by step introducing more and more
of the used attributes of different natures that add details to the virtual 
declaration of `\declare`.

At the bare minimum `\declare` is declared as: 

        \define[\nature define \is define]
        \define[\nature define \is nature]
        \define[\nature alias \is is \accept ?]
        
Read 1: the element `define` is of nature `define`
Read 2: the attribute `nature` is of nature `define`
Read 3: the attribute `is` is of nature `alias` (only accepting names that are not bound so far `?`)

Comparing 1 and 2 it might become apparent that there is nothing so far that
differs making 1 an element and 2 an attribute. This indeed isn't clear and
will require further attributes that are examined later. 
So the nature `define` is used both as an element `\define` and as the
attribute `\nature`. To once more make the point clear the identifiers used
don't really matter. What matters is that they are linked to the nature `define`.
A link that can only be established with the help of an element like `\define` 
and two attributes like `\nature` to link and `is` to name.

## About `implicit`:

When certain attributes are essential there is a desire to express this more
elegantly, more concise. The `implicit` nature helps doing this by providing
a list of attribute names which are assumed for arguments should they omit
the attribute. 

Assuming `\define` is declared with:

        \define[\nature define \is define \implicit [nature is]]

The following then is equivalent:

        \define[define define \implicit [nature is]]
        
as the first two naked values are inferred to be for `\nature` and `\is`.        

Now, how is `\implicit` then defined?

        \define[implicit implicit \in define]
        
Read: the attribute `implicit` is of nature `implicit` and occurs `in` the attributes of the element `\define`

## About `in`:

As seen above the `in` nature allows to infer where elements or attributes occur.

For `is` it would be declared:

        \define[alias is \in &define]
        
Read: `is` has the function of an `alias` when used in element with nature `define`        
        
whereas for `define` and `nature` it would state

        \define[define define \in ^]
        \define[define nature \in define]

where `^` is a special marker for the top level of a document. `in` itself
would be defined as:

        \define[in in \in define]
        
Read: `in` (2nd arg) is a meta attribute of type `in` (1st arg) and does
occur in the element `define`.

## About `around`:

An `\define` element should never wrap around other elements when nesting isn't made clear by using `{...}`.
This is declared by the empty set `[]`:

		\define[define define \in ^ \around []]


## About `must`:

The `define` element needs the `nature` attribute and an `alias`. 
This is declared as:

        \define[define define \in ^ \must [&define &alias]]
        
Read: the `define` element must have some attribute _that is of nature_ (`&`)
`define` and some attribute that is of nature `alias`. 
When in contrast one wanted to say it must have an attribute that is exactly of
the identity `x` the `&` is omitted and just the plain name `x` is stated.
So in the case of this example `nature` is used as this attribute. 
It is indeed a bootstrapped (not build in = hard coded) attribute defined as:

        \define[define nature \in &define]


## About `may`:

Completing the `define` element there are several attributes that may occur.
This is declared as `\may`:

        \define[define define 
            \implicit [nature is] 
            \in ^ 
            \must [&define &alias] 
            \may [&in &is-a &must &may &implicit &plain &inline]
        ]

		 \define[alias is \accept ?]        

## About `is-a`
The `is-a` nature creates elements or attributes that semantically _extend_ 
an already existing one.
All e/a involved (including the defined one) have to be of the same nature.

        \define[text block]
        \define[\is section \is-a block]
        
The first defines a `text` element `block`, the second line defines a `section`
which is said to be a `block` so it may take its place.

The 2nd line above can also be written as:

        \block*[section]
        
Read: A new (type of) `block` is _born_ called `section`. 
This is simply a syntax level expansion. 
Note that the nature is derived from the referenced type in both cases.
This also means a `must` is evaluated not the statement made but the end-effect
it has.

Elements can extend multiple existing ones. The attributes are _inherited_ in
the order of definition. Once an attribute is set, it is not overridden. 
Attributes directly defined in a definition always override inherited ones.

TODO which attributes inherit? only some natures? give user control via new nature?

TODO how to distinguish between wanting the exact named element and one that _is a_ element? => e.g. `block` vs `*block` where `*` is indicating the "is a" relation by reading: _any_ `block` 



## About `inline`

The inline formatting is the unnamed element `\{}`. When defined quotes `""` are 
used to declare the empty name.

        \define[text "" \implicit is-a]

The nature of the default inline element is not `inline` but `text` as it is
a text element. The `inline` nature is only used to bind other elements to
the empty name.
Also the default uses a implicit `is-a` (`\is-a` of nature `is-a`). Which means
when using it like this:

        The \[b]{brown} fox
        
the argument `b` is the `is-a` argument making the `text` element into a `b` text
element, which we could assume mean _bold_ similar to HTML. This would have been
equivalent to:

        The \b{brown} fox        




## About `mark`
Below example creates an attribute `\anchor`, an element `\mark` to mark text positions and an element `\ref` to refer to marked text positions 


		\define[alias anchor in &mark]
		\define[mark mark \in &text \around &text \implicit anchor]
		\define[ref ref \in &text \accept &mark \implicit @]

Idea: the `@` is used to refer to the element itself, so when the element should be used as attribute within its own list of attributes one uses `@` (value) or `\@` (key)

Idea: the way a `ref` is printed is declared using the body of the declaration => templating

Element use:

		The \mark[A]{brown fox} quickly

Reference:

		See \ref[A]

Idea: When a reference should be printed a special element of nature `text` is used which uses a pattern of some sorts that explains how to use the attribute data to produce the text that should be printed.
      Both styles of references, pure meta (as in `ref` example above) and as text (predefined on content level) might be useful, as for some it might be better to leave the visualisation to the presentation system whereas other should be present in a specific determined way that is part of the content.

TODO how are footnotes build with this?
TODO how are references to bibliography build with this?      		



## Practical Notes
Bootstrap starting with a minimal `\define` which is redefined several times
to add attributes that just became available as they were defined themselves.

To redefine a name in a namespace one uses `\define!` with a `!`.
This means for the interpreter that there already has to be a definition of the
same name and that it is replaced. Otherwise the interpreter will complain.

This also will allow cross references where the attributes used with an element reference the element and the element reference the attribute.
Such circular referencing can be build using redefine where first only a bare element is defined. Then its attributes are defined before the element is redefined with all attribute details.
This just means internal representation must be name based and avoid pointers that would need updating.


# Templating
Body of the `define` element can be used as a template where the attributes are accessible using `\$`. 
For example attribute `\key` value is accessed as `\$key`

Idea: nature `var` for `$`?

In some way then the `implicit` attribute is of nature `text` this is like the template:

		\define[... \implicit \heading]
		\define[... \implicit key]{
			\heading[\$key]
		}


# Namespaces
Are a way to organize requirements and to resolve name collisions. 
That are definitions made outside of a document that are used by the document.

Definition:

        \define[ns foo \requires [one other]]
        \define[ref requires \in define]

Read: the `ns` `foo` requires definitions made in namespaces `one` and `other`
Read 2: the `ref`(erence) `requires` is used in the `define` element

When utilising the _born as a_ syntax namespaces usually are created like:

        \ns*[foo]

To add definitions to the namespace it is used as an element.

        \foo{
            \define[...]
            \define[...]
            #...        
        }

The above definitions now are linked to the `foo` namespace.

TODO can namespace elements only be used within the file they are defined in?
     are they simply not accessible because they are not themselves included in
     their own namespace hence there is no way to import them as an element?

Usage:

        \ns[foo]
        
where `ns` element is defined as

        \define[ref use \in &ns \accept &ns]
        \define[ns ns \in * \around * \implicit use]
        
        
Read 1: the `ref`(erence) `use` is used in element `ns` (to point to the used `ns`)

Read 2: `ns` is of nature `ns`, can be used anywhere, wraps anything and its first
unnamed argument is of type `use` (of nature `ref`).

This implies:
* switching into a namespace is done by an *element* of nature `ns` with an
  `alias` nature attribute referring to the namespace to switch to
* namespaces can occur anywhere. When not used at the top level (where they span
  the entire document, the block spanned needs to be marked using `{...}`

Usage to resolve name collisions:

        \ns[foo] {
            # usage of elements as found when starting the lookup from foo
        
            \ns[bar] {
                # usage of elements as found when starting the lookup from bar
            }
        }
        
As namespaces enclose around any other element a full document can be enclosed
simply by starting that document with

        \ns[foo]
        
The curly braces are not needed and inferred for the full document.
When multiple namespace are used one can simply state all of them in a list.

        \ns[[foo bar]]

The double `[[]]` might look a bit silly but it is because this is just short for:

        \ns[\use [foo bar]]


TODO How does one define in `^` when namespace is wrapping around? 
Define `define`'s `in` as `&ns` instead of `^`? making sure they always occur in a namespace? 
or both `\in [^ $ns]`, meaning in top level or in any element that is of nature `ns`

## Imports
Idea: could be elements before the use of a `ns[foo]` which establish where
foo might be found. Their nature is unclear but usage would be like:

        \import[foo http://...]
        \import[foo http://...]
        \ns[[foo bar]]
        
The `import` can also be of nature `ns`. In contrast to the `ns` which uses a 
reference only the `import` has a `ref` and a external ref (nature not decided yet).

## Disambiguate
When names exist in multiple imported namespaces one can qualify the ns before 
the name of the element or attribute

        \foo:element[\foo:attribute]
        
        

# Styles
Styles are per target system and target element

        \define[ref selector \in html \accept &text]
        \define[style html \implicit selector]
        \define[style tag \in html]
        \define[style css \in html]
        \html[[chapter heading] \tag h1 \css "font-size: 14pt;"]

First the `selector` reference `ref` is defined that points to `text` elements.
Then a `style` target `html` is defined. The key is that it uses a `ref` attribute.
The `tag` and `css` `style` attributes are defined (as an example).
To bind the `style` key-values to a specific element the style target `html` is used as element
where first the target `rel` is given followed by the actual `style` attributes that should be attached.
The `selector` for the reference is not understood as a set of possible reference targets
but as a hierarchy, so in this case the target are all `heading` elements directly below a `chapter`.
The style details of this example would be linked to the `heading` element but should only be
applied by html renderers if the `heading` in an actual document is directly nested in a `chapter` element.
