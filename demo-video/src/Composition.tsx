import {
  AbsoluteFill,
  Easing,
  Img,
  interpolate,
  staticFile,
  useCurrentFrame,
} from "remotion";
import scenes from "./full-demo-scenes.json";
import captions from "./full-demo-captions.json";

type Scene = {
  title: string;
  kicker: string;
  body: string;
  image?: string;
  accent: string;
  metrics?: Array<{ label: string; value: string }>;
};

const sceneFrames = 120;

const fitImage = (image: string) => {
  if (image.includes("buyer")) {
    return { objectFit: "cover" as const, objectPosition: "top center" };
  }
  if (image.includes("admin")) {
    return { objectFit: "cover" as const, objectPosition: "top center" };
  }
  return { objectFit: "contain" as const, objectPosition: "center" };
};

const Caption = ({ text, progress }: { text: string; progress: number }) => {
  const opacity = interpolate(progress, [0, 0.12, 0.88, 1], [0, 1, 1, 0], {
    extrapolateLeft: "clamp",
    extrapolateRight: "clamp",
    easing: Easing.bezier(0.16, 1, 0.3, 1),
  });

  return (
    <div
      style={{
        position: "absolute",
        left: 64,
        right: 64,
        bottom: 40,
        display: "flex",
        justifyContent: "center",
        opacity,
      }}
    >
      <div
        style={{
          maxWidth: 900,
          borderRadius: 8,
          padding: "14px 24px",
          background: "rgba(19, 15, 19, 0.82)",
          color: "#fffaf6",
          fontSize: 28,
          fontWeight: 700,
          lineHeight: 1.25,
          textAlign: "center",
          boxShadow: "0 18px 40px rgba(0, 0, 0, 0.24)",
        }}
      >
        {text}
      </div>
    </div>
  );
};

const SceneCard = ({
  scene,
  caption,
  localFrame,
}: {
  scene: Scene;
  caption: string;
  localFrame: number;
}) => {
  const progress = localFrame / sceneFrames;
  const intro = interpolate(progress, [0, 0.18], [36, 0], {
    extrapolateLeft: "clamp",
    extrapolateRight: "clamp",
    easing: Easing.bezier(0.16, 1, 0.3, 1),
  });
  const opacity = interpolate(progress, [0, 0.08, 0.92, 1], [0.96, 1, 1, 0.96], {
    extrapolateLeft: "clamp",
    extrapolateRight: "clamp",
    easing: Easing.bezier(0.16, 1, 0.3, 1),
  });
  const imageScale = interpolate(progress, [0, 1], [1.02, 1.08], {
    extrapolateLeft: "clamp",
    extrapolateRight: "clamp",
  });

  return (
    <AbsoluteFill
      style={{
        opacity,
        background:
          "linear-gradient(135deg, #140f14 0%, #261314 42%, #fff7f0 42%, #fffaf7 100%)",
        overflow: "hidden",
      }}
    >
      <div
        style={{
          position: "absolute",
          left: 0,
          top: 0,
          width: 470,
          height: 720,
          padding: "70px 54px",
          color: "#fff9f5",
          transform: `translateX(${intro * -0.45}px)`,
        }}
      >
        <div
          style={{
            width: 64,
            height: 6,
            borderRadius: 6,
            background: scene.accent,
            marginBottom: 34,
          }}
        />
        <div
          style={{
            fontSize: 22,
            color: "#ffcfbd",
            fontWeight: 700,
            marginBottom: 18,
          }}
        >
          {scene.kicker}
        </div>
        <h1
          style={{
            margin: 0,
            fontSize: 54,
            lineHeight: 1.08,
            letterSpacing: 0,
          }}
        >
          {scene.title}
        </h1>
        <p
          style={{
            marginTop: 28,
            fontSize: 25,
            lineHeight: 1.55,
            color: "rgba(255, 250, 246, 0.82)",
          }}
        >
          {scene.body}
        </p>
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "1fr 1fr",
            gap: 12,
            marginTop: 36,
          }}
        >
          {(scene.metrics || []).map((metric) => (
            <div
              key={metric.label}
              style={{
                borderRadius: 8,
                padding: "16px 14px",
                background: "rgba(255, 255, 255, 0.1)",
                border: "1px solid rgba(255, 255, 255, 0.12)",
              }}
            >
              <div style={{ fontSize: 24, fontWeight: 800 }}>{metric.value}</div>
              <div style={{ fontSize: 14, color: "#ffcfbd", marginTop: 4 }}>
                {metric.label}
              </div>
            </div>
          ))}
        </div>
      </div>

      <div
        style={{
          position: "absolute",
          left: 500,
          top: 54,
          right: 54,
          bottom: 92,
          borderRadius: 8,
          background: "#ffffff",
          boxShadow: "0 34px 90px rgba(80, 40, 30, 0.22)",
          overflow: "hidden",
          transform: `translateY(${intro}px)`,
        }}
      >
        {scene.image ? (
          <Img
            src={staticFile(scene.image)}
            style={{
              width: "100%",
              height: "100%",
              transform: `scale(${imageScale})`,
              ...fitImage(scene.image),
            }}
          />
        ) : (
          <div
            style={{
              height: "100%",
              display: "grid",
              placeItems: "center",
              padding: 56,
              background:
                "linear-gradient(135deg, #ff5b78 0%, #ff8562 55%, #fff2df 100%)",
              color: "#fff",
              textAlign: "center",
            }}
          >
            <div>
              <div style={{ fontSize: 74, fontWeight: 900, letterSpacing: 0 }}>
                玩偶循环
              </div>
              <div style={{ fontSize: 30, marginTop: 18, fontWeight: 700 }}>
                二手玩偶交易系统
              </div>
            </div>
          </div>
        )}
      </div>

      <Caption text={caption} progress={progress} />
    </AbsoluteFill>
  );
};

export const MyComposition = () => {
  const frame = useCurrentFrame();
  const index = Math.min(
    Math.floor(frame / sceneFrames),
    (scenes as Scene[]).length - 1,
  );
  const localFrame = frame - index * sceneFrames;
  const scene = (scenes as Scene[])[index];
  const caption = (captions as string[])[index] || scene.title;

  return <SceneCard scene={scene} caption={caption} localFrame={localFrame} />;
};
