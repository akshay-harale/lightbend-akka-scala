resolvers in ThisBuild += "lightbend-commercial-mvn" at
  "https://repo.lightbend.com/pass/-1zQSa_qhv5S3ctPhS040rhsy7fOJWKjLQuM1U6fBPQYLksp/commercial-releases"
resolvers in ThisBuild += Resolver.url("lightbend-commercial-ivy",
  url("https://repo.lightbend.com/pass/-1zQSa_qhv5S3ctPhS040rhsy7fOJWKjLQuM1U6fBPQYLksp/commercial-releases"))(Resolver.ivyStylePatterns)