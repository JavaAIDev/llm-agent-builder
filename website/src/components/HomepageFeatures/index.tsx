import Link from "@docusaurus/Link";
import BoltIcon from "@mui/icons-material/Bolt";
import HandymanIcon from "@mui/icons-material/Handyman";
import MemoryIcon from "@mui/icons-material/Memory";
import PreviewIcon from "@mui/icons-material/Preview";
import PsychologyIcon from "@mui/icons-material/Psychology";
import RocketLaunchIcon from "@mui/icons-material/RocketLaunch";
import clsx from "clsx";
import styles from "./styles.module.css";

type FeatureItem = {
  title: string;
  image: JSX.Element;
  link: string;
  description: JSX.Element;
};

const iconSx = {
  fontSize: 120,
};

const FeatureList: FeatureItem[] = [
  {
    title: "Quick Start",
    image: <RocketLaunchIcon sx={iconSx} />,
    link: "/docs/quickstart/intro",
    description: <>Quick start with Spring Boot</>,
  },
  {
    title: "Planner",
    image: <PsychologyIcon sx={iconSx} />,
    link: "/docs/planner/intro",
    description: <>Built-in planners: Simple, ReAct, Structured chat</>,
  },
  {
    title: "Chat Memory",
    image: <MemoryIcon sx={iconSx} />,
    link: "/docs/memory/intro",
    description: <>Chat memory as message history</>,
  },
  {
    title: "Tools",
    image: <HandymanIcon sx={iconSx} />,
    link: "/docs/tools/intro",
    description: <>Agent tools</>,
  },
  {
    title: "Observation",
    image: <PreviewIcon sx={iconSx} />,
    link: "/docs/observation/intro",
    description: <>Observation support, tracing and metrics</>,
  },
  {
    title: "Build & Deploy",
    image: <BoltIcon sx={iconSx} />,
    link: "/docs/build-deploy/intro",
    description: <>Build & Deploy agents</>,
  },
];

function Feature({ title, link, image, description }: FeatureItem) {
  return (
    <div className={clsx("col col--4")}>
      <div className="text--center">
        <Link to={link}>{image}</Link>
      </div>
      <div className="text--center padding-horiz--md">
        <Link to={link}>
          <h1>{title}</h1>
        </Link>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures(): JSX.Element {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
