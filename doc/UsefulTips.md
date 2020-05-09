Useful Tips
===========

## Add license header to files by name mask

```shell script
$ find . -name \*.java -exec sh -c "mv '{}' tmp && cp util/template/license_header_common '{}' && cat tmp >> '{}' && rm tmp" \;
$ find . -name \*.py -exec sh -c "mv '{}' tmp && cp util/template/license_header_other '{}' && cat tmp >> '{}' && rm tmp" \;
```

* [license_header_common](../util/template/license_header_common) template applies to the C, C++, Java source files.
* [license_header_other](../util/template/license_header_other) template applies to the Python and other source files.

These commands should be run at the root of the project.
