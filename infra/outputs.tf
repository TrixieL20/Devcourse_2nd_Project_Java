output "s3_bucket_name" {
  description = "S3 bucket name"
  value       = aws_s3_bucket.image_bucket.bucket
}

output "s3_bucket_arn" {
  description = "S3 bucket ARN"
  value       = aws_s3_bucket.image_bucket.arn
}

output "s3_bucket_region" {
  description = "S3 bucket region"
  value       = aws_s3_bucket.image_bucket.region
}