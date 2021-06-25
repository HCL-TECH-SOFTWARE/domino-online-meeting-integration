# coding: utf-8

Gem::Specification.new do |spec|
    spec.name          = "project"
    spec.version       = "1.0.2"
    spec.authors       = ["Paul Withers"]
    spec.email         = ["paulstephen.withers@hcl.com"]
  
    spec.summary       = %q{Testing theme Just-the-Docs}
    spec.homepage      = ""
    spec.license       = ""
  
    spec.files         = `git ls-files -z`.split("\x0").select { |f| f.match(%r{^(assets|bin|_layouts|_includes|lib|Rakefile|_sass|LICENSE|README)}i) }
  
    spec.add_development_dependency "bundler", "~> 2.2.4"
    spec.add_runtime_dependency "jekyll", ">= 3.8.5"
    spec.add_runtime_dependency "jekyll-seo-tag", "~> 2.7"
    spec.add_runtime_dependency "rake", ">= 12.3.1", "< 13.1.0"
    spec.add_runtime_dependency "webrick", "~> 1.7"
    spec.add_runtime_dependency "just-the-docs", "~> 0.3.3"
  
  end
  