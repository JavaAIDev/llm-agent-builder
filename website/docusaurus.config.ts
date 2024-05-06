import { themes as prismThemes } from "prism-react-renderer";
import type { Config } from "@docusaurus/types";
import type * as Preset from "@docusaurus/preset-classic";

const config: Config = {
  title: "LLM Agent Builder",
  tagline: "LLM Agent Powered by Java / Spring AI",
  favicon: "img/favicon.ico",

  // Set the production url of your site here
  url: "https://llmagentbuilder.github.io",
  // Set the /<baseUrl>/ pathname under which your site is served
  // For GitHub pages deployment, it is often '/<projectName>/'
  baseUrl: "/llm-agent-builder/",

  // GitHub pages deployment config.
  // If you aren't using GitHub pages, you don't need these.
  organizationName: "LLMAgentBuilder", // Usually your GitHub org/user name.
  projectName: "llm-agent-builder", // Usually your repo name.
  trailingSlash: false,

  onBrokenLinks: "warn",
  onBrokenMarkdownLinks: "warn",

  // Even if you don't use internationalization, you can use this field to set
  // useful metadata like html lang. For example, if your site is Chinese, you
  // may want to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: "en",
    locales: ["en"],
  },

  presets: [
    [
      "classic",
      {
        docs: {
          sidebarPath: "./sidebars.ts",
        },
        blog: {
          showReadingTime: true,
        },
        theme: {
          customCss: "./src/css/custom.css",
        },
      } satisfies Preset.Options,
    ],
  ],

  themeConfig: {
    metadata: [
      {
        name: "keywords",
        content: "java, llm, llm-agent, spring-ai, spring-boot",
      },
    ],
    navbar: {
      title: "LLM Agent Builder",
      logo: {
        alt: "LLM Agent Builder",
        src: "img/logo.png",
      },
      items: [
        {
          type: "docSidebar",
          sidebarId: "quickstart",
          position: "left",
          label: "Quick Start",
        },
        {
          type: "docSidebar",
          sidebarId: "planner",
          position: "left",
          label: "Planner",
        },
        {
          type: "docSidebar",
          sidebarId: "memory",
          position: "left",
          label: "Memory",
        },
        {
          type: "docSidebar",
          sidebarId: "tools",
          position: "left",
          label: "Tools",
        },
        {
          type: "docSidebar",
          sidebarId: "observation",
          position: "left",
          label: "Observation",
        },
        {
          type: "docSidebar",
          sidebarId: "build-deploy",
          position: "left",
          label: "Build & Deploy",
        },
        { to: "/blog", label: "Blog", position: "left" },
        {
          href: "https://github.com/alexcheng1982/llm-agent-builder",
          label: "GitHub",
          position: "right",
        },
      ],
    },
    footer: {
      style: "dark",
      links: [
        {
          title: "Docs",
          items: [
            {
              label: "Quick Start",
              to: "/docs/quickstart/intro",
            },
          ],
        },
        {
          title: "Community",
          items: [],
        },
        {
          title: "More",
          items: [
            {
              label: "Blog",
              to: "/blog",
            },
            {
              label: "GitHub",
              href: "https://github.com/LLMAgentBuilder/llm-agent-builder",
            },
          ],
        },
      ],
      copyright: `Copyright © ${new Date().getFullYear()} Fu Cheng`,
    },
    prism: {
      theme: prismThemes.github,
      darkTheme: prismThemes.dracula,
      additionalLanguages: ["java", "kotlin"],
    },
  } satisfies Preset.ThemeConfig,
};

export default config;
