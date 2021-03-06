Contributions are yet accepted to this repository.

## Web Application

To contribute to the web application, please ensure you follow the Code Cleanup settings and code formatter profile in the webapp directory.

Never store credentials in the source code, always pass in as environment variables.

Please ensure you test locally first - you will need your own OAuth apps for the relevant meeting provider(s) and client IDs and secrets.

## DOMI Notes Application

When pushing changes to an On Disk Project, ensure the "Use Binary DXL for source control operations" preference (Preferences > Domino Designer > Source Control) is switched **off**.

Do not change the `release` directory content, we will handle that when processing the pull request.

## Documentation

Please test locally, if possible, before pushing changes. As all GitHub sites, it uses Jekyll and so there are dependencies on Ruby and Bundler. To load the gems, go to the "docs" directory in a Terminal and issue `bundle install`. To run the site use `bundle exec jekyll serve`.
