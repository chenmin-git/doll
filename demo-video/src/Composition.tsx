import {
  AbsoluteFill,
  Easing,
  Img,
  interpolate,
  staticFile,
  useCurrentFrame,
} from "remotion";
import scenes from "./full-demo-scenes.json";

type Scene = {
  step: string;
  title: string;
  caption: string;
  image: string;
  role: string;
  focus: string;
};

const sceneFrames = 105;

const sceneCount = (scenes as Scene[]).length;

const imageFit = () => {
  return { objectFit: "cover" as const, objectPosition: "top center" };
};

const BrowserShell = ({ scene, frame }: { scene: Scene; frame: number }) => {
  const progress = frame / sceneFrames;
  const scale = interpolate(progress, [0, 1], [1.012, 1.045], {
    extrapolateLeft: "clamp",
    extrapolateRight: "clamp",
  });
  const y = interpolate(progress, [0, 0.14], [28, 0], {
    extrapolateLeft: "clamp",
    extrapolateRight: "clamp",
    easing: Easing.bezier(0.16, 1, 0.3, 1),
  });

  return (
    <div
      style={{
        position: "absolute",
        left: 46,
        right: 46,
        top: 76,
        bottom: 126,
        borderRadius: 8,
        overflow: "hidden",
        background: "#ffffff",
        boxShadow: "0 34px 90px rgba(31, 20, 16, 0.32)",
        transform: `translateY(${y}px)`,
      }}
    >
      <div
        style={{
          height: 34,
          background: "linear-gradient(180deg, #fbf8f5, #eee8e3)",
          borderBottom: "1px solid rgba(36, 24, 20, 0.12)",
          display: "flex",
          alignItems: "center",
          gap: 8,
          paddingLeft: 16,
        }}
      >
        {["#ff6b6b", "#f7bf42", "#3ecf8e"].map((color) => (
          <span
            key={color}
            style={{ width: 11, height: 11, borderRadius: 999, background: color }}
          />
        ))}
        <div
          style={{
            marginLeft: 18,
            height: 18,
            width: 280,
            borderRadius: 999,
            background: "#fff",
            color: "#8b7b73",
            fontSize: 11,
            display: "flex",
            alignItems: "center",
            paddingLeft: 14,
          }}
        >
          localhost:3000
        </div>
      </div>
      <Img
        src={staticFile(scene.image)}
        style={{
          width: "100%",
          height: "calc(100% - 34px)",
          transform: `scale(${scale})`,
          transformOrigin: "top center",
          ...imageFit(),
        }}
      />
    </div>
  );
};

const BottomBar = ({ scene, frame }: { scene: Scene; frame: number }) => {
  const progress = frame / sceneFrames;
  const opacity = interpolate(progress, [0, 0.12, 0.9, 1], [0, 1, 1, 0], {
    extrapolateLeft: "clamp",
    extrapolateRight: "clamp",
  });
  const y = interpolate(progress, [0, 0.16], [28, 0], {
    extrapolateLeft: "clamp",
    extrapolateRight: "clamp",
    easing: Easing.bezier(0.16, 1, 0.3, 1),
  });

  return (
    <div
      style={{
        position: "absolute",
        left: 54,
        right: 54,
        bottom: 34,
        height: 78,
        display: "grid",
        gridTemplateColumns: "170px 1fr 260px",
        alignItems: "center",
        gap: 22,
        opacity,
        transform: `translateY(${y}px)`,
      }}
    >
      <div
        style={{
          height: 56,
          borderRadius: 8,
          background: "linear-gradient(135deg, #ff5b78, #ff8562)",
          color: "#fff",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          fontSize: 24,
          fontWeight: 900,
        }}
      >
        {scene.step}
      </div>
      <div
        style={{
          borderRadius: 8,
          background: "rgba(19, 15, 19, 0.86)",
          color: "#fffaf6",
          padding: "16px 24px",
          boxShadow: "0 18px 44px rgba(0, 0, 0, 0.18)",
        }}
      >
        <div style={{ fontSize: 25, fontWeight: 900, lineHeight: 1.1 }}>
          {scene.title}
        </div>
        <div
          style={{
            fontSize: 18,
            lineHeight: 1.35,
            color: "rgba(255, 250, 246, 0.78)",
            marginTop: 8,
          }}
        >
          {scene.caption}
        </div>
      </div>
      <div
        style={{
          height: 56,
          borderRadius: 8,
          background: "#fff7ef",
          border: "1px solid rgba(255, 105, 101, 0.22)",
          color: "#432924",
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          paddingLeft: 20,
        }}
      >
        <div style={{ fontSize: 13, color: "#a16c61", fontWeight: 700 }}>
          当前角色
        </div>
        <div style={{ fontSize: 20, fontWeight: 900 }}>{scene.role}</div>
      </div>
    </div>
  );
};

const CursorCue = ({ scene, frame }: { scene: Scene; frame: number }) => {
  const pulse = Math.sin((frame / 30) * Math.PI * 2) * 0.5 + 0.5;
  const opacity = interpolate(frame, [10, 24, 84, 98], [0, 1, 1, 0], {
    extrapolateLeft: "clamp",
    extrapolateRight: "clamp",
  });

  return (
    <div
      style={{
        position: "absolute",
        right: 72,
        top: 22,
        display: "flex",
        alignItems: "center",
        gap: 12,
        opacity,
      }}
    >
      <div
        style={{
          borderRadius: 999,
          padding: "9px 16px",
          background: "rgba(255, 255, 255, 0.92)",
          color: "#3f2b28",
          fontSize: 17,
          fontWeight: 800,
          boxShadow: "0 12px 28px rgba(42, 20, 16, 0.14)",
        }}
      >
        {scene.focus}
      </div>
      <div
        style={{
          width: 22,
          height: 22,
          borderRadius: 999,
          border: "4px solid #ff5b78",
          boxShadow: `0 0 0 ${8 + pulse * 8}px rgba(255, 91, 120, ${0.18 - pulse * 0.08})`,
          background: "#fff",
        }}
      />
    </div>
  );
};

export const MyComposition = () => {
  const frame = useCurrentFrame();
  const index = Math.min(Math.floor(frame / sceneFrames), sceneCount - 1);
  const localFrame = frame - index * sceneFrames;
  const scene = (scenes as Scene[])[index];
  const fade = interpolate(localFrame, [0, 10, sceneFrames - 10, sceneFrames], [0.9, 1, 1, 0.92], {
    extrapolateLeft: "clamp",
    extrapolateRight: "clamp",
  });

  return (
    <AbsoluteFill
      style={{
        background:
          "radial-gradient(circle at 20% 15%, rgba(255, 91, 120, 0.18), transparent 26%), linear-gradient(135deg, #171013 0%, #2a1515 52%, #fff7ef 52%, #fffaf7 100%)",
        opacity: fade,
        overflow: "hidden",
      }}
    >
      <BrowserShell scene={scene} frame={localFrame} />
      <CursorCue scene={scene} frame={localFrame} />
      <BottomBar scene={scene} frame={localFrame} />
    </AbsoluteFill>
  );
};
