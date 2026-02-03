/** @type {import('next').NextConfig} */
const nextConfig = {
  images: {
    // MVP: skip Next.js image optimisation to reduce CPU on the VPS.
    // Remove this line and configure a proper loader when moving to production.
    unoptimized: true,

    // Allow <Image> to pull from any S3 bucket in any region.
    remotePatterns: [
      {
        hostname: '*.s3.*.amazonaws.com',
      },
    ],
  },
};

export default nextConfig;
