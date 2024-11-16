.PHONY: chmod-gradlew format format-check test cover-report quality-check checkup gen-gpg-key clean-gpg-keys \
publish-github publish-space publish-maven publish clean

permission: # Give permission to execute gradlew.
	git update-index --chmod=+x gradlew && chmod -R 777 script/

format: # Format code with spotless.
	./script/format.sh

format-check: # 🔬 Check code format with spotless.
	./script/format-check.sh

test: # 🩺 Run all tests.
	./script/test.sh

cover-report: # Generate code coverage report.
	./script/cover-report.sh

quality-check: # 🔬 Check code quality with sonar.
	./script/quality-check.sh

checkup: format test quality-check  # Code format, test and quality check.

gen-gpg-key: # Generate gpg key.
	./script/gen-gpg-key.sh

clean-gpg-keys: # Clean all gpg keys.
	./script/clean-gpg-keys.sh

publish-github-packages: checkup # 🚀 Publish to GitHub Packages.
	./script/publish-github-packages.sh

publish-space-packages: checkup # 🚀 Publish to Space Packages.
	./script/publish-space-packages.sh

publish-maven: checkup # 🚀 Publish to Maven.
	./script/publish-maven.sh

publish: checkup # 🚀 Publish to Space Packages, GitHub Packages and Maven.
	./script/publish-github.sh && ./script/publish-space.sh && ./script/publish-maven.sh

clean: # Clean all.
	./script/clean.sh

server-auto-reload: # Server application hot reload
  ./script/server-auto-reload.sh
