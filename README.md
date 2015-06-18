
# gokb-integrations

A set of scripts for checking journal titles in GoKB against CrossRef, Road ISSN and Sherpa/Romeo APIs.

## strategy

These tools are designed to work from the command line and to support traditional Unix pipeline
processing (e.g. java GOKbIntegrations/MakeDataset CUFTS/* | java GOKbIntegrations/QuerySherpaRomeo > dataset.json).

+ MakeDataset - the start of the pipeline
+ QuerySherpaRomeo - take a file (or stdin) containing a JSON array of objects with title, issn and e_issn and query the Sherpa Romeo API adding license info.


